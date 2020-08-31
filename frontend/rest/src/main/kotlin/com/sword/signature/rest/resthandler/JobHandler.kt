package com.sword.signature.rest.resthandler


import com.sword.signature.api.job.Job
import com.sword.signature.api.job.JobFile
import com.sword.signature.api.merkel.MerkelTree
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.service.FileService
import com.sword.signature.business.service.JobService
import com.sword.signature.business.service.SignService
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.rest.data.pagedSorted
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@RequestMapping("\${api.base-path:/api}")
class JobHandler(
    val signService: SignService,
    val jobService: JobService,
    val fileService: FileService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/jobs"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun jobs(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestParam(value = "id", required = false) id: String?,
        @RequestParam(value = "accountId", required = false) accountId: String?,
        @RequestParam(value = "flowName", required = false) flowName: String?,
        @RequestParam(value = "dateBegin", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateStart: LocalDate?,

        @RequestParam(value = "dateEnd", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateEnd: LocalDate?,

        @RequestParam(value = "channel", required = false) channelName: String?,
        @RequestParam(value = "sort", required = false) sort: List<String>?,
        @RequestParam(value = "page", required = false) page: Int?,
        @RequestParam(value = "size", required = false) size: Int?
    ): Flow<Job> {

        val paged = pagedSorted(page, size, sort)

        val criteria = JobCriteria(
            accountId = accountId,
            id = id,
            flowName = flowName,
            dateStart = dateStart,
            dateEnd = dateEnd,
            channelName = channelName
        )

        return jobService.findAll(user.account, criteria, paged).map { it.toWeb() }
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/jobs-count"],
            produces = ["application/json"],
            method = [RequestMethod.GET]
    )
    suspend fun jobCount(
            @AuthenticationPrincipal user: CustomUserDetails,
            @RequestParam(value = "id", required = false) id: String?,
            @RequestParam(value = "accountId", required = false) accountId: String?,
            @RequestParam(value = "flowName", required = false) flowName: String?,
            @RequestParam(value = "dateBegin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateStart: LocalDate?,

            @RequestParam(value = "dateEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateEnd: LocalDate?,

            @RequestParam(value = "channel", required = false) channelName: String?
    ): Long {

        val criteria = JobCriteria(
                accountId = accountId,
                id = id,
                flowName = flowName,
                dateStart = dateStart,
                dateEnd = dateEnd,
                channelName = channelName
        )

        return jobService.countAll(user.account, criteria)
    }


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
                val proof = fileService.getFileProof(requester = user.account, fileId = leaf.id)
                    ?: throw EntityNotFoundException("file", leaf.id)

                emit(leaf.toWeb(proof))
            }
        }
    }


    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/jobs/{jobId}/merkelTree"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun merkelTree(
        @AuthenticationPrincipal user: CustomUserDetails,
        @Parameter(description = "job Id") @PathVariable(value = "jobId") jobId: String
    ): MerkelTree {
        val tree = jobService.getMerkelTree(requester = user.account, jobId = jobId)
            ?: throw EntityNotFoundException("job", jobId)
        return tree.toWeb()

    }



}


