package com.sword.signature.rest.resthandler

import com.sword.signature.api.job.JobFile
import com.sword.signature.api.proof.Proof
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.FileFilter
import com.sword.signature.business.service.FileService
import com.sword.signature.rest.data.pagedSorted
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("\${api.base-path:/api}")
class FileHandler(
    private val fileService: FileService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/file/{fileId}/proof"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getProof(
        @AuthenticationPrincipal requester: CustomUserDetails,
        @PathVariable("fileId") fileId: String
    ): Proof {
        val proof: com.sword.signature.business.model.Proof =
            fileService.getFileProof(requester = requester.account, fileId = fileId).awaitFirstOrNull()
                ?: throw EntityNotFoundException("file", fileId)
        return proof.toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/files"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getFiles(
        @AuthenticationPrincipal requester: CustomUserDetails,
        @RequestParam(value = "id", required = false) id: String?,
        @RequestParam(value = "name", required = false) name: String?,
        @RequestParam(value = "hash", required = false) hash: String?,
        @RequestParam(value = "jobId", required = false) jobId: String?,
        @RequestParam(value = "accountId", required = false) accountId: String?,
        @RequestParam(value = "dateStart", required = false) dateStart: LocalDate?,
        @RequestParam(value = "dateEnd", required = false) dateEnd: LocalDate?,
        @RequestParam(value = "sort", required = false) sort: List<String>?,
        @RequestParam(value = "page", required = false) page: Int?,
        @RequestParam(value = "size", required = false) size: Int?
    ): Flow<JobFile> {
        val filter = FileFilter(
            id = id,
            name = name,
            hash = hash,
            jobId = jobId,
            accountId = accountId,
            dateStart = dateStart,
            dateEnd = dateEnd
        )
        val paged = pagedSorted(page, size, sort)
        val files = fileService.getFiles(requester = requester.account, filter = filter, pageable = paged).asFlow()

        return files.map { it.toWeb() }
    }
}