package com.sword.signature.ui.webhandler

import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.Job
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*
import java.io.File
import java.time.LocalDateTime

@Controller
class SignatureHandler(
    private val signService: SignService,
    private val algorithmService: AlgorithmService
) {

    suspend fun timestamping(request: ServerRequest): ServerResponse {
        val model = mutableMapOf<String, Any>()
        model["fileToUpload"] = mutableListOf<File>()
        model["algorithms"] = algorithmService.findAll().toList().map { it.name }
        return ServerResponse.ok().html().renderAndAwait("signature/timestamping", model)
    }

    suspend fun timestamp(request: ServerRequest): ServerResponse {
        val account = request.getAccount() ?: throw IllegalAccessException("not connected")
        val formData = request.awaitFormData()

        val algorithmName = formData["algorithm"]?.get(0)
        val algorithm = algorithmService.getAlgorithmByName(algorithmName!!)

        val fileNames = formData["file-name"]
        val fileSizes = formData["file-size"]
        val fileHashes = formData["file-hash"]
        val fileComments = formData["file-comment"]
        val batchComments = formData["batch-comment"]

        val batchComment =
            if (!batchComments.isNullOrEmpty() && batchComments[0].isNotBlank()) batchComments[0] else null
        val fileNumber = fileNames?.size ?: 0

        val files = mutableListOf<Pair<String, FileMetadata>>()

        for (i in 0 until fileNumber) {
            // Build custom fields
            val customFields = mutableMapOf<String, String>()
            batchComment?.let { customFields.put("batchComment", it) }
            if (fileComments!![i].isNotBlank()) {
                customFields["fileComment"] = fileComments[i]
            }

            files.add(
                Pair(
                    fileHashes!![i],
                    FileMetadata(
                        fileName = fileNames!![i],
                        fileSize = fileSizes!![i],
                        customFields = if (customFields.isNotEmpty()) customFields else null
                    )
                )
            )
        }
        var jobs = listOf<Job>()
        if (files.isNotEmpty()) {

            jobs = signService.batchSign(
                requester = account,
                algorithm = algorithm,
                flowName = "web_" + LocalDateTime.now().toString(),
                fileHashs = files.asFlow()
            ).toList()
            LOGGER.info("Jobs: {}", jobs)

        }
        if (jobs.isNotEmpty()) {
            return ServerResponse.ok().html().renderAndAwait("redirect:/jobs/" + jobs[0].id)
        }
        return ServerResponse.ok().html().renderAndAwait("redirect:/timestamping")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SignatureHandler::class.java)
    }
}