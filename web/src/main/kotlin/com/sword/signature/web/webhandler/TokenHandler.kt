package com.sword.signature.web.webhandler

import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.service.TokenService
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
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
        return ServerResponse.ok().html().renderAndAwait("tokens/tokens", model)
    }

    suspend fun addToken(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        val model = mutableMapOf<String, Any>()
        model["token"] = TokenCreate("", null, account.id)
        return ServerResponse.ok().html().renderAndAwait("tokens/add-token", model)
    }

    @RequestMapping("/createToken")
    suspend fun createToken(createToken: Any, result: BindingResult, model: Model) : ServerResponse {
        if(result.hasErrors()) {
            return ServerResponse.ok().html().renderAndAwait("tokens/add-token")
        }
        return ServerResponse.ok().html().renderAndAwait("tokens/tokens")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenHandler::class.java)
    }
}