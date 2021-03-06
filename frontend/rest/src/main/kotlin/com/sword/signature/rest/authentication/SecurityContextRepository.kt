package com.sword.signature.rest.authentication

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class SecurityContextRepository(
    private val authenticationManager: ReactiveAuthenticationManager
) : ServerSecurityContextRepository {


    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
      throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {

        val request = swe.request
        val authHeader =
            request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val authToken = authHeader.substring(7)
            val auth: Authentication = BearerTokenAuthenticationToken( authToken)
            authenticationManager.authenticate(auth)
                .map { authentication -> SecurityContextImpl(authentication) }
        } else {
            Mono.empty()
        }
    }
}