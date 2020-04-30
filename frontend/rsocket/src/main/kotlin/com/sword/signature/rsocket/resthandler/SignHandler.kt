package com.sword.signature.web.resthandler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.web.mapper.toBusiness
import com.sword.signature.web.mapper.toWeb
import com.sword.signature.web.webhandler.getAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*
import java.lang.IllegalArgumentException

@Controller
class SignHandler(
    val accountService: AccountService,
    val signService: SignService,
    val algorithmService: AlgorithmService
) {

    suspend fun sign(request: ServerRequest): ServerResponse {
        val account = request.getAccount() ?: throw IllegalAccessException("not connected")
        val algorithmParameter =
            request.queryParamOrNull("algorithm") ?: throw IllegalArgumentException("missing algorithm parameter")
        val flowName =
            request.queryParamOrNull("flowName") ?: throw IllegalArgumentException("missing flowName parameter")

        val algorithm = algorithmService.getAlgorithmByName(algorithmParameter)
        val body = request.bodyToFlow<SignRequest>()
        val jobs = signService.batchSign(account, algorithm, flowName, body.map { it.toBusiness() })
        return ServerResponse.ok().json().bodyAndAwait(jobs)
    }
}


