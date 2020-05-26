package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.daemon.sendPayload
import com.sword.signature.tezos.service.TezosReaderService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

@Component
class ValidationJob(
    private val tezosReaderService: TezosReaderService,
    private val jobService: JobService,
    private val validationRetryMessageChannel: MessageChannel,
    private val callBackMessageChannel: MessageChannel,
    @Value("\${daemon.validation.minDepth}") private val minDepth: Long
) {
    private val adminAccount = Account(email = "", login = "", password = "", isAdmin = true, fullName = "", id = "")

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

        if (transactionDepth != null && transactionDepth > minDepth) {
            LOGGER.info("Transaction {} for job {} validated.", transactionHash, jobId)
            val job = jobService.patch(
                requester = adminAccount,
                jobId = jobId,
                patch = JobPatch(
                    state = JobStateType.VALIDATED
                )
            )
            // Adding callback after job
            if (job.callBackUrl != null) {
                callBackMessageChannel.sendPayload(CallBackJobMessagePayload(jobId = job.id, url = job.callBackUrl!!))
            }
        } else {
            if (transactionDepth != null) {
                LOGGER.info(
                    "{}/{} confirmations found for transaction {} (job={}).", transactionDepth, minDepth,
                    transactionHash, jobId
                )
            } else {
                LOGGER.info("No confirmation found for transaction {} (job={}) yet.", transactionHash, jobId)
            }
            // Set the validation for retry later.
            validationRetryMessageChannel.sendPayload(payload)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ValidationJob::class.java)
    }
}