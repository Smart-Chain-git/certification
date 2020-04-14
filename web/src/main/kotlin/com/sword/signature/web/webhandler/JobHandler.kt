package com.sword.signature.web.webhandler

import com.sword.signature.business.service.JobService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait

@Controller
class JobHandler(
        private val jobService: JobService
) {

    suspend fun jobs(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        val jobs = jobService.findAllByUser(requester = account, account = account)
        val model = mapOf<String, Any>(
                "jobs" to jobs
        )
        return ServerResponse.ok().html().renderAndAwait("jobs/jobs", model)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JobHandler::class.java)
    }
}