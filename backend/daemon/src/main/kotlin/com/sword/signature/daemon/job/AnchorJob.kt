package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.daemon.logger
import com.sword.signature.daemon.sendPayload
import com.sword.signature.tezos.service.TezosWriterService
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

@Component
class AnchorJob(
    private val jobService: JobService,
    private val tezosWriterService: TezosWriterService,
    private val anchoringRetryMessageChannel: MessageChannel,
    private val validationMessageChannel: MessageChannel,
    @Value("\${tezos.contract.address}") private val contractAddress: String
) {

    private val adminAccount = Account(email = "", login = "", password = "", isAdmin = true, fullName = "", id = "")

    suspend fun anchor(payload: AnchorJobMessagePayload) {
        val jobId = payload.jobId
        val job = jobService.findById(adminAccount, jobId, true)
        val rootHash = job?.rootHash ?: throw IllegalStateException("An existing job must have a root hash")
        LOGGER.debug("Ready to anchor job ({}) rootHash ({}) into the blockchain.", jobId, rootHash)
        try {
            val transactionHash = tezosWriterService.anchorHash(rootHash)
            val injectedJob = jobService.patch(
                requester = adminAccount,
                jobId = jobId,
                patch = JobPatch(
                    transactionHash = transactionHash,
                    state = JobStateType.INJECTED,
                    numberOfTry = job.numberOfTry + 1,
                    contractAddress = contractAddress
                )
            )
            validationMessageChannel.sendPayload(
                ValidationJobMessagePayload(
                    jobId = jobId,
                    transactionHash = transactionHash,
                    injectionTime = injectedJob.injectedDate!!
                )
            )
        } catch (e: Exception) {
            LOGGER.error("Anchoring of job ({}) failed.", jobId, e)
            // Set the anchoring for retry later.
            anchoringRetryMessageChannel.sendPayload(payload)
            throw e
        }

    }

    companion object {
        val LOGGER = logger()
    }
}

