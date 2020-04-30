package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.rest.authentication.CustomUserDetails
import com.sword.signature.rest.mapper.toBusiness
import com.sword.signature.rest.mapper.toWeb
import com.sword.signature.rest.webhandler.getAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.*
import java.lang.IllegalArgumentException

@RestController
//@Api(value = "Bt", description = "The Bt API")
@RequestMapping("\${api.base-path:/api}")
class SignHandler(
    val signService: SignService,
    val algorithmService: AlgorithmService
) {


    @RequestMapping(
        value = ["/sign"],
        produces = ["application/json"],
        consumes = ["application/json"],
        method = [RequestMethod.POST])
    suspend fun sign(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestParam(value = "algorithm") algorithmParameter: String?,
        @RequestParam(value = "flowName") flowName: String?,
        @RequestBody requests : Flow<SignRequest>
    ): Flow<SignResponse> {
        if(algorithmParameter==null) {throw IllegalArgumentException("missing algorithm parameter")}

        if(flowName==null){ throw IllegalArgumentException("missing flowName parameter")}

        val algorithm = algorithmService.getAlgorithmByName(algorithmParameter)

        val jobs = signService.batchSign(user.account, algorithm, flowName, requests.map { it.toBusiness() })
        return jobs.map { it.toWeb() }
    }
}


