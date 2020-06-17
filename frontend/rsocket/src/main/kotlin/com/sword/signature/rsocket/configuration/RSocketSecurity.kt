package com.sword.signature.rsocket.configuration

import com.sword.signature.api.sign.SignMimeTypes.*
import com.sword.signature.webcore.authentication.SignatureAuthenticationManager
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.MetadataExtractorRegistry
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.metadataToExtract
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.util.MimeType

@Configuration
@ComponentScan(basePackages = ["com.sword.signature"])
@EnableRSocketSecurity
class RSocketSecurity {

    @Bean
    fun rSocketMessageHandler(rSocketStrategies: RSocketStrategies): RSocketMessageHandler {
        val mh = RSocketMessageHandler()
        mh.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        mh.rSocketStrategies = rSocketStrategies
        return mh
    }

    @Bean
    fun rSocketStrategiesCustomizer() = RSocketStrategiesCustomizer {
        it.metadataExtractorRegistry { registry: MetadataExtractorRegistry ->
            registry.metadataToExtract<String>(MimeType.valueOf(ALGORITHM_MIME_TYPE.value), "algorithm")
            registry.metadataToExtract<String>(MimeType.valueOf(FLOW_NAME_MIME_TYPE.value), "flowName")
            registry.metadataToExtract<String>(MimeType.valueOf(CALLBACK_URL_MIME_TYPE.value), "callBackUrl")
        }
    }

    @Bean
    fun payloadSocketAcceptorInterceptor(
        security: RSocketSecurity,
        reactiveAuthenticationManager: SignatureAuthenticationManager
    ): PayloadSocketAcceptorInterceptor {
        return security
            .authorizePayload { spec: AuthorizePayloadsSpec ->
                spec
                    .setup().hasRole("SETUP")
                    .route("newJobs").authenticated()
                    .anyExchange().permitAll()
            }
            .jwt { jwtSpec ->
                jwtSpec.authenticationManager(reactiveAuthenticationManager)
            }
            .build()
    }
}
