package com.sword.signature.web.authentication

import com.sword.signature.business.service.TokenService
import com.sword.signature.web.configuration.UserDetailsService
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SignatureAuthenticationManager(
        private val tokenService: TokenService,
        private val userDetailsService: UserDetailsService
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val bearerToken = (authentication as BearerTokenAuthenticationToken).token

        val token = runBlocking { tokenService.checkAndGetToken(bearerToken) }
        val user = userDetailsService.findById(token.accountId)

        return Mono.just(SignatureAuthenticationToken(
                principal = user,
                credentials = token,
                authorities = user.authorities))
    }
}