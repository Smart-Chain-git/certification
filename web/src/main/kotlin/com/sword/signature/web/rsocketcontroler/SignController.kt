package com.sword.signature.web.rsocketcontroler

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.SignService
import com.sword.signature.web.mapper.toBusiness
import com.sword.signature.web.mapper.toWeb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
class SignController(
    val accountService: AccountService,
    val signService : SignService
) {

    @MessageMapping("newJobs")
    suspend fun batchSign(@AuthenticationPrincipal user: UserDetails, requests: Flow<SignRequest>): Flow<SignResponse> {
        LOGGER.debug("recu de {} ", user.username)
        val account = accountService.getAccountByLoginOrEmail(user.username) ?:throw AccountNotFoundException(user.username)
        return signService.batchSign( account, requests.map { it.toBusiness()}).map { it.toWeb() }
    }


    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SignController::class.java)
    }
}


