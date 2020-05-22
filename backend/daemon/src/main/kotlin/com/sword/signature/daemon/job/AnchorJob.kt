package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.daemon.logger
import com.sword.signature.daemon.sendPayload
import com.sword.signature.tezos.service.TezosWriterService
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

@Component
class AnchorJob(
    private val jobService: JobService,
    private val tezosWriterService: TezosWriterService,
    private val callBackMessageChannel: MessageChannel
) {

    private val adminAccount = Account(email = "", login = "", password = "", isAdmin = true, fullName = "", id = "")

    suspend fun anchor(payload: AnchorJobMessagePayload) {
        val jobId = payload.id
        val job = jobService.findById(adminAccount, jobId, true)
        val rootHash = job?.rootHash ?: throw IllegalStateException("An existing job must have a root hash")
        LOGGER.debug("Ready to anchor job ({}) rootHash ({}) into the blockchain.", jobId, rootHash)
        try {
            val transactionHash = tezosWriterService.anchorHash(rootHash)
            jobService.patch(
                adminAccount,
                jobId,
                JobPatch(transactionHash = transactionHash, state = JobStateType.INJECTED)
            )
        } catch (e: Exception) {
            LOGGER.error("Anchoring of job ({}) failed.", jobId, e)
            throw e
        }

        //adding callback after job
        if (job.callBackUrl != null) {
            callBackMessageChannel.sendPayload(CallBackJobMessagePayload(jobId = job.id, url = job.callBackUrl!!))
        }

    }

    companion object {
        val LOGGER = logger()
    }
}

