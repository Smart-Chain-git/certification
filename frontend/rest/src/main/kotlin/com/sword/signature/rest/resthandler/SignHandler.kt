package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toBusiness
import com.sword.signature.webcore.mapper.toWebSignResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException

@RestController
@RequestMapping("\${api.base-path:/api}")
class SignHandler(
    val signService: SignService,
    val algorithmService: AlgorithmService
) {

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/sign"],
        produces = ["application/json"],
        consumes = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun sign(
        @AuthenticationPrincipal user: CustomUserDetails,
        @Parameter(
            description = "algorithm used for checksum",
            required = true
        ) @RequestParam(value = "algorithm") algorithmParameter: String?,
        @Parameter(
            description = "name of the flow",
            required = true
        ) @RequestParam(value = "flowName") flowName: String?,
        @Parameter(description = "callback URL") @RequestParam(value = "callBack") callBack: String?,
        @RequestBody requests: Flow<SignRequest>
    ): Flow<SignResponse> {
        if (algorithmParameter == null) {
            throw IllegalArgumentException("missing algorithm parameter")
        }

        if (flowName == null) {
            throw IllegalArgumentException("missing flowName parameter")
        }

        val algorithm = algorithmService.getAlgorithmByName(algorithmParameter)

        val jobs = signService.batchSign(user.account, algorithm, flowName, requests.map { it.toBusiness() })
        return jobs.map { it.toWebSignResponse() }
    }
}


