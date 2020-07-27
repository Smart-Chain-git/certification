package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.enums.JobStateType
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class RecoverJob(
    private val jobService: JobService,
    private val anchoringRetryMessageChannel: MessageChannel
) {
    private val adminAccount = Account(
        id = "", login = "", password = "", fullName = "", email = "", company = null,
        country = null, publicKey = null, hash = null, isAdmin = true, disabled = false, firstLogin = false
    )

    fun recoverInjectedJobs() {
        val injectedJobs =
            runBlocking { jobService.findAll(adminAccount, JobCriteria(jobState = JobStateType.INSERTED)).toList() }
        LOGGER.info("{} injected jobs retrieved at startup.", injectedJobs.size)

        for (job in injectedJobs) {
            anchoringRetryMessageChannel.send(
                MessageBuilder.withPayload(
                    AnchorJobMessagePayload(
                        requesterId = job.userId,
                        jobId = job.id
                    )
                ).build()
            )
        }
        LOGGER.info("{} injected jobs recovered.", injectedJobs.size)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecoverJob::class.java)
    }
}