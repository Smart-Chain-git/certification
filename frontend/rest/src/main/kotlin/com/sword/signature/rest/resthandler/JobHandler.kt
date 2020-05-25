package com.sword.signature.rest.resthandler


import com.sword.signature.api.sign.Job
import com.sword.signature.api.sign.JobFile
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.service.JobService
import com.sword.signature.business.service.SignService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("\${api.base-path:/api}")
class JobHandler(
    val signService: SignService,
    val jobService: JobService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/jobs/{jobId}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun job(
        @AuthenticationPrincipal user: CustomUserDetails,
        @Parameter(description = "job Id") @PathVariable(value = "jobId") jobId: String
    ): Job {
        val job =
            jobService.findById(requester = user.account, jobId = jobId) ?: throw EntityNotFoundException("job", jobId)
        return job.toWeb()

    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/jobs/{jobId}/files"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun jobFiles(
        @AuthenticationPrincipal user: CustomUserDetails,
        @Parameter(description = "job Id") @PathVariable(value = "jobId") jobId: String
    ): Flow<JobFile> {
        val job = jobService.findById(requester = user.account, jobId = jobId, withLeaves = true)
            ?: throw EntityNotFoundException("job", jobId)
        return flow {
            job.files?.forEach { leaf ->
                val proof = signService.getFileProof(requester = user.account, fileId = leaf.id)
                    ?: throw EntityNotFoundException("file", leaf.id)

                emit(leaf.toWeb(proof))
            }
        }
    }


}


