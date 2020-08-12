package com.sword.signature.daemon.job

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.daemon.sendPayload
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime

@Component
class ValidationJob(
    private val accountService: AccountService,
    private val tezosReaderService: TezosReaderService,
    private val jobService: JobService,
    private val validationRetryMessageChannel: MessageChannel,
    private val callbackMessageChannel: MessageChannel,
    private val anchoringMessageChannel: MessageChannel,
    @Value("\${tezos.validation.minDepth}") private val minDepth: Long,
    @Value("\${daemon.validation.timeout}") private val validationTimeout: Duration
) {

    suspend fun validate(payload: ValidationJobMessagePayload) {
        val requesterId = payload.requesterId
        val jobId = payload.jobId
        val transactionHash = payload.transactionHash

        try {
            LOGGER.debug("Starting validation for job {} with transaction {}.", jobId, transactionHash)

            val transactionDepth = tezosReaderService.getTransactionDepth(transactionHash)
            LOGGER.debug("Depth of {} found for transaction {}.", transactionDepth, transactionHash)

            if (transactionDepth != null) {
                val requester: Account =
                    accountService.getAccount(requesterId) ?: throw EntityNotFoundException("account", requesterId)

                if (transactionDepth >= minDepth) {
                    LOGGER.info("Transaction {} for job {} validated.", transactionHash, jobId)

                    // Retrieving the transaction
                    val transaction: TzOp = tezosReaderService.getTransaction(transactionHash)
                        ?: throw IllegalStateException("The transaction ($transactionHash) should not be null there since its depth has been found before.")
                    val job = jobService.patch(
                        requester = requester,
                        jobId = jobId,
                        patch = JobPatch(
                            timestamp = transaction.bigMapDiff[0].value.timestamp,
                            state = JobStateType.VALIDATED,
                            blockHash = transaction.block,
                            blockDepth = transactionDepth
                        )
                    )
                    // Call the callback url
                    if (job.callBackUrl != null) {
                        callbackMessageChannel.sendPayload(
                            CallBackJobMessagePayload(
                                jobId = job.id,
                                url = job.callBackUrl!!
                            )
                        )
                    }
                } else {
                    LOGGER.info(
                        "{}/{} confirmations found for transaction {} (job={}).", transactionDepth, minDepth,
                        transactionHash, jobId
                    )

                    // Update transaction depth.
                    jobService.patch(
                        requester = requester,
                        jobId = jobId,
                        patch = JobPatch(
                            blockDepth = transactionDepth
                        )
                    )
                    // Set the validation for retry later.
                    validationRetryMessageChannel.sendPayload(payload)
                }
            } else {
                if (OffsetDateTime.now() > payload.injectionTime.plus(validationTimeout)) {
                    LOGGER.info(
                        "No confirmation found after {}m for transaction {} (jobId={}). Anchoring will be retried...",
                        validationTimeout.toMinutes(), transactionHash, jobId
                    )
                    anchoringMessageChannel.sendPayload(
                        AnchorJobMessagePayload(
                            requesterId = requesterId,
                            jobId = jobId
                        )
                    )
                } else {
                    LOGGER.info("No confirmation found for transaction {} (job={}) yet.", transactionHash, jobId)
                    // Set the validation for retry later.
                    validationRetryMessageChannel.sendPayload(payload)
                }
            }
        } catch (e: Exception) {
            // Set the validation for retry later.
            validationRetryMessageChannel.sendPayload(payload)
            throw e
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ValidationJob::class.java)
    }
}
