package com.sword.signature.web.webhandler

import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.merkletree.utils.hexStringHash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
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
        model["algorithms"] = listOf("SHA-256", "SHA-1")
        model["timestampData"] = TimestampData()
        return ServerResponse.ok().html().renderAndAwait("signature/timestamping", model)
    }

    suspend fun timestamp(request: ServerRequest): ServerResponse {
        val multipartData = request.awaitMultipartData()
        val algorithmName: String = (multipartData["algorithm"]?.get(0) as FormFieldPart).value()
        val algorithm = algorithmService.getAlgorithmByName(algorithmName)

        val files = (multipartData["files"] as List<FilePart>?)?.filter {
            !it.filename().isNullOrEmpty()
        }?.map { file ->
            Pair(
                hexStringHash(
                    algorithm.name,
                    file.content().awaitFirst().asInputStream().readAllBytes()
                ), file.filename()
            )
        }
        if(files != null && files.isNotEmpty()) {
            signService.batchSign(
                requester = request.getAccount(),
                algorithm = algorithm,
                flowName = "web_" + LocalDateTime.now().toString(),
                fileHashs = files.asFlow()
            )
        }
        return ServerResponse.ok().html().renderAndAwait("redirect:/timestamping")
    }

    data class TimestampData(
        val algorithm: String = "SHA-1",
        val files: MutableList<MultipartFile> = mutableListOf()
    ) {
    }
}