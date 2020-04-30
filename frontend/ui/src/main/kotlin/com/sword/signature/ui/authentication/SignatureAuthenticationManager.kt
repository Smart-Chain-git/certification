package com.sword.signature.ui.authentication

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("signatureAuthManager")
class SignatureAuthenticationManager(
    private val userDetailsService: UserDetailsService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ReactiveAuthenticationManager {

    /**
     * Authenticate the user using a token or username/password.
     * @param authentication Bearer token containing the JWT token for authentication or username/password token.
     * @return Authentication composed of the authenticated user with its token/password and authorities.
     */
    override fun authenticate(authentication: Authentication): Mono<Authentication> {

       return mono {
           authentication as UsernamePasswordAuthenticationToken

            val user = userDetailsService.findByUsername(authentication.principal as String).awaitFirst()

            if (!bCryptPasswordEncoder.matches(authentication.credentials as CharSequence, user.password)) {
                throw BadCredentialsException("Invalid credentials")
            }

            SignatureAuthenticationToken(
                principal = user,
                credentials = user.password,
                authorities = user.authorities
            )

        }

    }
}
