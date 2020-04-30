package com.sword.signature.rest.authentication

import com.sword.signature.business.service.TokenService
import com.sword.signature.web.authentication.UserDetailsService
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("signatureAuthManager")
class SignatureAuthenticationManager(
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsService
) : ReactiveAuthenticationManager {

    /**
     * Authenticate the user using a token or username/password.
     * @param authentication Bearer token containing the JWT token for authentication or username/password token.
     * @return Authentication composed of the authenticated user with its token/password and authorities.
     */
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return mono {
            authentication as BearerTokenAuthenticationToken

            val bearerToken = authentication.token

            val token =  tokenService.getAndCheckToken(bearerToken)
            val user = userDetailsService.findById(token.accountId)


            SignatureAuthenticationToken(
                principal = user,
                credentials = token,
                authorities = user.authorities
            )


        }
    }
}