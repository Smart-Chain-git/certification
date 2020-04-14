package com.sword.signature.web.webhandler

import com.sword.signature.business.service.TokenService
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait

@Controller
class TokenHandler(
        private val tokenService: TokenService
) {

    suspend fun tokens(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        val model = mutableMapOf<String, Any>()
        model["tokens"] = tokenService.getTokensByAccountId(account.id).toList()
        return ServerResponse.ok().html().renderAndAwait("tokens", model)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenHandler::class.java)
    }
}