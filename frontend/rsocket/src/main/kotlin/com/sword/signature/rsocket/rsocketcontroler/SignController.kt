package com.sword.signature.rsocket.rsocketcontroler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.webcore.mapper.toBusiness
import com.sword.signature.webcore.mapper.toWebSignResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
class SignController(
    val accountService: AccountService,
    val signService: SignService,
    val algorithmService: AlgorithmService
) {

    @MessageMapping("newJobs")
    fun batchSign(
        @AuthenticationPrincipal user: UserDetails,
        @Header(name = "algorithm") algorithmParameter: String?,
        @Header(name = "flowName") flowName: String?,
        @Header(name = "callBackUrl") callBackUrl: String?,
        requests: Flow<SignRequest>
    ): Flow<SignResponse> {

        if (algorithmParameter == null) {
            throw IllegalArgumentException("missing algorithm parameter")
        }

        if (flowName == null) {
            throw IllegalArgumentException("missing flowName parameter")
        }

        // BOF BIDOUILLE
        // je fait un runBlocking pour appeler un truc non bloquant
        // car sinon  le return de mon flow me lance une erreur de netty
        // c'ets tolerable car on bloque que sur un petit appel de la table des utilisateur et des algo
        val account = runBlocking {
            accountService.getAccountByLoginOrEmail(user.username) ?: throw AccountNotFoundException(user.username)
        }


        val algorithm = runBlocking { algorithmService.getAlgorithmByName(algorithmParameter) }

        return signService.batchSign(account, algorithm, flowName, callBackUrl, requests.map { it.toBusiness() })
            .map { it.toWebSignResponse() }
    }
}
