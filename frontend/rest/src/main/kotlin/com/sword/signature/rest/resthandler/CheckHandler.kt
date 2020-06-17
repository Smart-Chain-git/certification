package com.sword.signature.rest.resthandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.sword.signature.api.check.CheckOutput
import com.sword.signature.api.check.CheckRequest
import com.sword.signature.api.proof.Proof
import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.service.CheckService
import com.sword.signature.webcore.mapper.toBusiness
import com.sword.signature.webcore.mapper.toWeb
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.*
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path:/api}")
class CheckHandler(
    val checkService: CheckService,
    val objectMapper: ObjectMapper
) {

    @RequestMapping(
        value = ["/check"],
        produces = ["application/json"],
        consumes = ["multipart/form-data"],
        method = [RequestMethod.POST]
    )
    suspend fun checkDocument(
        @RequestPart("documentHash", required = true) documentHash: String,
        @RequestPart("proof", required = false) proofFile: FilePart?
    ): CheckOutput {
        val proof = proofFile?.let {
            it.content().reduce(InputStream.nullInputStream()) { s: InputStream, d ->
                SequenceInputStream(s, d.asInputStream())
            }.map { s ->
                objectMapper.readValue(s, Proof::class.java)
            }.awaitFirstOrNull()
        }
        return try {
            checkService.checkDocument(documentHash, proof?.toBusiness()).toWeb()
        } catch (e: CheckException) {
            e.toWeb()
        } catch (e: Exception) {
            throw e
        }
    }

    @RequestMapping(
        value = ["/check"],
        produces = ["application/json"],
        consumes = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun checkDocumentJson(
        @RequestBody checkRequest: CheckRequest
    ): CheckOutput {

        val proof = checkRequest.proof?.let {
            objectMapper.convertValue(Base64.getDecoder().decode(it), Proof::class.java)
        }

        return try {
            checkService.checkDocument(checkRequest.documentHash, proof?.toBusiness()).toWeb()
        } catch (e: CheckException) {
            e.toWeb()
        } catch (e: Exception) {
            throw e
        }
    }
}
