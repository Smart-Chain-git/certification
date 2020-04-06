package com.sword.signature.web.authentication

import com.sword.signature.business.service.TokenService
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

    /**
     * Authenticate the user using a token.
     * @param authentication Bearer token containing the JWT token for authentication.
     * @return Authentication composed of the authenticated user with its token and authorities.
     */
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