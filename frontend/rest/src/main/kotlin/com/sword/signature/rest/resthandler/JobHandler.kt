package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.Branch
import com.sword.signature.api.sign.Job
import com.sword.signature.api.sign.JobFile
import com.sword.signature.api.sign.Proof
import com.sword.signature.webcore.authentication.CustomUserDetails
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
import java.time.OffsetDateTime
import java.util.*


@RestController
@RequestMapping("\${api.base-path:/api}")
class JobHandler {

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

        return Job(
            id = jobId,
            algorithm = "SHA-256",
            createdDate = OffsetDateTime.now(),
            flowName = "monFlow",
            state = "INJECTED",
            stateDate = OffsetDateTime.now()
        )
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
        return flow {
            for (cpt in 1..10) {
                emit(
                    JobFile(
                        id = UUID.randomUUID().toString(),
                        hash = "hashOfAFile",
                        jobId = jobId,
                        fileName = "fichier_$cpt",
                        proof = Proof(
                            algorithm = "SHA-256",
                            publicKey = "ZpublicKey",
                            originPublicKey = "ZoriginPublicKey",
                            branch = Branch(
                                hash = "hashOfAFile",
                                position = "LEFT"
                            )
                        )

                    )
                )
            }
        }
    }


}


