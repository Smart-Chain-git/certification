package com.sword.signature.web.webhandler

import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.service.TokenService
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*
import java.time.LocalDate

@Controller
class TokenHandler(
    private val tokenService: TokenService
) {

    suspend fun tokens(request: ServerRequest): ServerResponse {
        val account = request.getAccount()
        val model = mutableMapOf<String, Any>()
        model["tokens"] = tokenService.getTokensByAccountId(account.id).toList()
        model["token"] = TokenDraft("", null)
        return ServerResponse.ok().html().renderAndAwait("tokens/tokens", model)
    }

    suspend fun addToken(request: ServerRequest): ServerResponse {
        val formData = request.awaitFormData()
        val tokenCreate = TokenCreate(
            name = formData["name"]?.get(0) ?: throw IllegalStateException("Error"),
            expirationDate = formData["expirationDate"]?.get(0)
                ?.let { if (it.isNotBlank()) LocalDate.parse(it) else null },
            accountId = request.getAccount().id
        )
        val token = tokenService.createToken(tokenCreate)
        return tokens(request)
    }

    suspend fun removeToken(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        tokenService.deleteToken(requester = request.getAccount(), tokenId = id)
        return tokens(request)
    }

    data class TokenDraft(
        val name: String,
        val expirationDate: LocalDate?
    ) {}

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenHandler::class.java)
    }
}