package com.sword.signature.web.webhandler

import com.sword.signature.business.service.JobService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*

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

    suspend fun job(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        val jobId = request.pathVariable("jobId")

        val job =
            jobService.findById(requester = account, jobId = jobId) ?: return ServerResponse.notFound().buildAndAwait()

        val model = mapOf<String, Any>(
            "job" to job
        )
        return ServerResponse.ok().html().renderAndAwait("jobs/job", model)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JobHandler::class.java)
    }
}