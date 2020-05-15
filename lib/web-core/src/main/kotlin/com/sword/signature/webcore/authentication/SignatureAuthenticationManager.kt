package com.sword.signature.webcore.authentication

import com.sword.signature.business.service.TokenService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Component
class SignatureAuthenticationManager(
    private val jwtTokenService: JwtTokenService,
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ReactiveAuthenticationManager {

    /**
     * Authenticate the user using a token or username/password.
     * @param authentication Bearer token containing the JWT token for authentication or username/password token.
     * @return Authentication composed of the authenticated user with its token/password and authorities.
     */
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return when (authentication) {
            is BearerTokenAuthenticationToken -> mono {
                val bearerToken = authentication.token

                val tokenInfo = jwtTokenService.parseToken(bearerToken)

                val user = if (tokenInfo.persisted) {
                    val token = tokenService.getAndCheckToken(bearerToken)
                    userDetailsService.findById(token.accountId)
                } else {
                    val now = OffsetDateTime.now()
                    if (tokenInfo.expirationTime?.isAfter(now) == true) {
                        userDetailsService.findById(tokenInfo.id)
                    } else {
                        throw BadCredentialsException("Expired token")
                    }

                }

                SignatureAuthenticationToken(
                    principal = user,
                    credentials = bearerToken,
                    authorities = user.authorities
                )
            }
            is UsernamePasswordAuthenticationToken -> mono {
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
            else -> throw IllegalArgumentException("Illegal authentication method.")
        }
    }
}