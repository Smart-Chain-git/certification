package com.sword.signature.web.rsocketcontroler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.web.mapper.toBusiness
import com.sword.signature.web.mapper.toWeb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import java.lang.IllegalArgumentException

@Controller
class SignController(
    val accountService: AccountService,
    val signService: SignService,
    val algorithmService: AlgorithmService
) {

    @MessageMapping("newJobs")
    suspend fun batchSign(
        @AuthenticationPrincipal user: UserDetails,
        @Header(name = "algorithm") algorithmParameter: String?,
        @Header(name = "flowName") flowName: String?,
        requests: Flow<SignRequest>
    ): Flow<SignResponse> {
        val account =
            accountService.getAccountByLoginOrEmail(user.username) ?: throw AccountNotFoundException(user.username)

        if (algorithmParameter == null) {
            throw IllegalArgumentException("missing algorithm parameter")
        }
        if (flowName == null) {
            throw IllegalArgumentException("missing flowName parameter")
        }

        val algorithm = algorithmService.getAlgorithmByName(algorithmParameter)

        return signService.batchSign(account, algorithm, flowName, requests.map { it.toBusiness() }).map { it.toWeb() }
    }
}


