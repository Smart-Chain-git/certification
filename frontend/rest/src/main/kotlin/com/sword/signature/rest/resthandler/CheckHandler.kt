package com.sword.signature.rest.resthandler

import com.fasterxml.jackson.core.JsonProcessingException
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
        return try {
            val proof = proofFile?.let {
                it.content().reduce(InputStream.nullInputStream()) { s: InputStream, d ->
                    SequenceInputStream(s, d.asInputStream())
                }.map { s ->
                    objectMapper.readValue(s, Proof::class.java)
                }.awaitFirstOrNull()
            }
            checkService.checkDocument(documentHash, proof?.toBusiness()).toWeb()
        } catch (e: CheckException) {
            e.toWeb()
        } catch (e: JsonProcessingException) {
            CheckException.IncorrectProofFile().toWeb()
        } catch (e: Exception) {
            throw e
        }
    }
}
