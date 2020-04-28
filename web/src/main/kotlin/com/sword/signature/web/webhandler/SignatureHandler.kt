package com.sword.signature.web.webhandler

import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.multipart.MultipartFile
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
        model["algorithms"] = listOf("MD5", "SHA-256", "SHA-1")
        model["timestampData"] = TimestampData()
        return ServerResponse.ok().html().renderAndAwait("signature/timestamping", model)
    }

    suspend fun timestamp(request: ServerRequest): ServerResponse {
        val formData = request.awaitFormData()
        val multipartData = request.awaitMultipartData()

        val algorithmName = formData["algorithm"]?.get(0)
        val algorithm = algorithmService.getAlgorithmByName(algorithmName!!)

        val fileNames = formData["file-name"]
        val fileSizes = formData["file-size"]
        val fileHashes = formData["file-hash"]
        val fileNumber = fileNames?.size ?: 0

        val files = mutableListOf<Pair<String, FileMetadata>>()

        for (i in 0 until fileNumber) {
            files.add(
                Pair(
                    fileHashes!![i],
                    FileMetadata(
                        fileName = fileNames!![i],
                        fileSize = fileSizes!![i],
                        fileComment = "Yolo"
                    )

                )
            )
        }
        if (files != null && files.isNotEmpty()) {

            val jobs = signService.batchSign(
                requester = request.getAccount(),
                algorithm = algorithm,
                flowName = "web_" + LocalDateTime.now().toString(),
                fileHashs = files.asFlow()
            ).toList()
            LOGGER.info("Jobs: {}", jobs)

        }
        return ServerResponse.ok().html().renderAndAwait("redirect:/timestamping")
    }

    data class TimestampData(
        val algorithm: String = "SHA-1",
        val files: MutableList<MultipartFile> = mutableListOf()
    ) {
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SignatureHandler::class.java)
    }
}