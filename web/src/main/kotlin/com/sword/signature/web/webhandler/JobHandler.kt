package com.sword.signature.web.webhandler

import com.sword.signature.business.service.JobService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait

@Component
class JobHandler(
    private val jobService: JobService
) {


    suspend fun jobs(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        LOGGER.info("{} demande ses jobs", account)
        val jobs=jobService.findAllByUser(account)
        val model = mapOf<String, Any>(
            "jobs" to jobs
        )
        return ServerResponse.ok().html().renderAndAwait("jobs/jobs", model)
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(JobHandler::class.java)
    }

}