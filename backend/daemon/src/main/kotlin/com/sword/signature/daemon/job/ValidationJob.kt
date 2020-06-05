package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
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
    private val tezosReaderService: TezosReaderService,
    private val jobService: JobService,
    private val validationRetryMessageChannel: MessageChannel,
    private val callbackMessageChannel: MessageChannel,
    private val anchoringMessageChannel: MessageChannel,
    @Value("\${tezos.validation.minDepth}") private val minDepth: Long,
    @Value("\${daemon.validation.timeout}") private val validationTimeout: Duration
) {
    private val adminAccount = Account(email = "", login = "", password = "", isAdmin = true, fullName = "", id = "", pubKey = null)

    suspend fun validate(payload: ValidationJobMessagePayload) {
        val jobId = payload.jobId
        val transactionHash = payload.transactionHash
        LOGGER.debug("Starting validation for job {} with transaction {}.", jobId, transactionHash)

        val transactionDepth: Long?
        try {
            transactionDepth = tezosReaderService.getTransactionDepth(transactionHash)
            LOGGER.debug("Depth of {} found for transaction {}.", transactionDepth, transactionHash)
        } catch (e: Exception) {
            LOGGER.error("Check that indexer is running and synchronized.")
            // Set the validation for retry later.
            validationRetryMessageChannel.sendPayload(payload)
            throw e
        }

        if (transactionDepth != null) {
            if (transactionDepth >= minDepth) {
                try {
                    LOGGER.info("Transaction {} for job {} validated.", transactionHash, jobId)
                    // Retrieving the transaction
                    val transaction: TzOp = tezosReaderService.getTransaction(transactionHash)
                        ?: throw IllegalStateException("The transaction ($transactionHash) should not be null there since its depth has been found before.")
                    val job = jobService.patch(
                        requester = adminAccount,
                        jobId = jobId,
                        patch = JobPatch(
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
                } catch (e: Exception) {
                    // Set the validation for retry later.
                    validationRetryMessageChannel.sendPayload(payload)
                    throw e
                }

            } else {
                LOGGER.info(
                    "{}/{} confirmations found for transaction {} (job={}).", transactionDepth, minDepth,
                    transactionHash, jobId
                )
                // Update transaction depth.
                jobService.patch(
                    requester = adminAccount,
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
                anchoringMessageChannel.sendPayload(AnchorJobMessagePayload(jobId = jobId))
            } else {
                LOGGER.info("No confirmation found for transaction {} (job={}) yet.", transactionHash, jobId)
                // Set the validation for retry later.
                validationRetryMessageChannel.sendPayload(payload)
            }

        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ValidationJob::class.java)
    }
}
