package com.sword.signature.web.configuration

import com.sword.signature.api.sign.ALGORITHM_MIME_TYPE
import com.sword.signature.api.sign.FLOW_NAME_MIME_TYPE
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer
import org.springframework.messaging.rsocket.MetadataExtractorRegistry
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.metadataToExtract
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.util.MimeType


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
                .route("newJobs").authenticated()
                .anyExchange().permitAll()
        }
        .simpleAuthentication(Customizer.withDefaults())
        .build()
}


fun rSocketStrategiesCustomizer() = RSocketStrategiesCustomizer {
    it.metadataExtractorRegistry { registry: MetadataExtractorRegistry ->
        registry.metadataToExtract<String>(MimeType.valueOf(ALGORITHM_MIME_TYPE), "algorithm")
        registry.metadataToExtract<String>(MimeType.valueOf(FLOW_NAME_MIME_TYPE), "flowName")
    }
}







