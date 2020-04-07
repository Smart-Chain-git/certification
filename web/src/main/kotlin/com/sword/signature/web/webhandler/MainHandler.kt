package com.sword.signature.web.webhandler

import com.sword.signature.business.model.Account
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.TokenService
import com.sword.signature.web.authentication.SignatureAuthenticationToken
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait

@Controller
class MainHandler(
        private val accountService: AccountService,
        private val tokenService: TokenService
) {

    suspend fun index(request: ServerRequest): ServerResponse {
        val model = mapOf<String, String>()
        return ServerResponse.ok().html().renderAndAwait("index", model)
    }

    suspend fun login(request: ServerRequest): ServerResponse {
        val model = mutableMapOf<String, String>()
        if (request.queryParam("error").isPresent) {
            model["loginError"] = "true"
        }
        return ServerResponse.ok().html().renderAndAwait("login", model)
    }

    suspend fun tokens(request: ServerRequest): ServerResponse {
        val model = mutableMapOf<String, Any>()
        val accountId = getMe(request).id
        model["tokens"] = tokenService.getTokensByAccountId(accountId).toList()
        return ServerResponse.ok().html().renderAndAwait("tokens", model)
    }

    private suspend fun getMe(request: ServerRequest) : Account {
        val authenticationToken = request.principal().awaitFirst() as SignatureAuthenticationToken
        return accountService.getAccountByLoginOrEmail((authenticationToken.principal as UserDetails).username) ?: throw IllegalStateException("User has to exists in current context.")
    }
}