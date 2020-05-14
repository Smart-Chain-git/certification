package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.tezos.service.TezosWriterService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AnchorJob(
    val jobService: JobService,
    val tezosWriterService: TezosWriterService
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
            LOGGER.error(e.toString())
            throw e
        }
    }

    companion object {
        val LOGGER = logger()
    }
}

inline fun <reified R : Any> R.logger(): Logger =
    LoggerFactory.getLogger(this::class.java.name.substringBefore("\$Companion"))