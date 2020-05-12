package com.sword.signature.daemon.job

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class AnchorJob(
    val jobService: JobService
) {

    private val adminAccount = Account(email = "", login = "", password = "", isAdmin = true, fullName = "", id = "")

    suspend fun anchor(payload: AnchorJobMessagePayload) {
        LOGGER.debug("falsely anchoring {} in the blockchain yves do your things here", payload.id)
        jobService.patch(adminAccount,payload.id, JobPatch(state = JobStateType.INJECTED))
    }


    companion object {
        val LOGGER = logger()
    }
}

inline fun <reified R : Any> R.logger(): Logger =
    LoggerFactory.getLogger(this::class.java.name.substringBefore("\$Companion"))