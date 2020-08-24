package com.sword.signature.rest.resthandler

import com.sword.signature.api.stat.DashBoardStat
import com.sword.signature.business.model.FileFilter
import com.sword.signature.business.service.FileService
import com.sword.signature.business.service.JobService
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.webcore.authentication.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path:/api}")
class StatHandler(
    val jobService: JobService,
    val fileService: FileService
) {


    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/stats/dashboard"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun dashboard(@AuthenticationPrincipal user: CustomUserDetails): DashBoardStat = coroutineScope {
        val jobCount = async { jobService.countAll(user.account, JobCriteria(accountId = user.account.id)) }
        val processedJobCount = async {
            jobService.countAll(
                user.account,
                JobCriteria(accountId = user.account.id, jobState = JobStateType.VALIDATED)
            )
        }
        val documentCount = async { fileService.countFiles(user.account, FileFilter(accountId = user.account.id)) }

        DashBoardStat(
            jobsCreatedCount = jobCount.await(),
            jobsProcessedCount = processedJobCount.await(),
            documentsSignedCount = documentCount.await()
        )
    }


}
