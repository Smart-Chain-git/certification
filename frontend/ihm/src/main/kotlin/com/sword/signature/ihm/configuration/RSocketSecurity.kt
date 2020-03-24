package com.sword.signature.ihm.configuration

import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor


fun rSocketMessageHandler(strategies: RSocketStrategies): RSocketMessageHandler {
    val mh = RSocketMessageHandler()
    mh.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
    mh.rSocketStrategies = strategies
    return mh
}

fun payloadSocketAcceptorInterceptor(security: RSocketSecurity): PayloadSocketAcceptorInterceptor {
    return security
        .authorizePayload { spec: AuthorizePayloadsSpec ->
            spec
                .route("greetings").authenticated()
                .anyExchange().permitAll()
        }
        .simpleAuthentication(Customizer.withDefaults())
        .build()
}