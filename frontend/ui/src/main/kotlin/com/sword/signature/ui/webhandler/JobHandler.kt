package com.sword.signature.ui.webhandler

import com.sword.signature.business.service.JobService
import com.sword.signature.business.service.SignService
import com.sword.signature.ui.mapper.toWeb
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*

@Controller
class JobHandler(
    private val signService: SignService,
    private val jobService: JobService
) {

    suspend fun jobs(request: ServerRequest): ServerResponse {
        val account = request.getAccount() ?: throw IllegalAccessException("not connected")
        val jobs = jobService.findAllByUser(requester = account, account = account)
        val model = mapOf<String, Any>(
            "jobs" to jobs
        )
        return ServerResponse.ok().html().renderAndAwait("jobs/jobs", model)
    }

    suspend fun job(request: ServerRequest): ServerResponse {
        val account = request.getAccount() ?: throw IllegalAccessException("not connected")
        val jobId = request.pathVariable("jobId")

        val job =
            jobService.findById(requester = account, jobId = jobId) ?: return ServerResponse.notFound().buildAndAwait()

        val model = mapOf<String, Any>(
            "job" to job
        )
        return ServerResponse.ok().html().renderAndAwait("jobs/job", model)
    }


    suspend fun fileProof(request: ServerRequest): ServerResponse {
        val account = request.getAccount() ?: throw IllegalAccessException("not connected")
        val fileId = request.pathVariable("fileId")
        LOGGER.debug("preuve pour {} fichier {}", account.login, fileId)
        val proof = signService.getFileProof(requester = account, fileId = fileId).toWeb()
        return if (proof == null) {
            ServerResponse.notFound().buildAndAwait()
        } else {
            ServerResponse.ok()
                .header("Content-Disposition", "attachment; filename=proof_$fileId.json")
                .json().bodyValueAndAwait(proof)
        }
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(JobHandler::class.java)
    }
}
