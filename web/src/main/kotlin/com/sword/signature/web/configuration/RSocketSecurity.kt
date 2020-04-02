package com.sword.signature.web.configuration

import com.auth0.jwt.interfaces.DecodedJWT
import com.sword.signature.api.sign.ALGORITHM_MIME_TYPE
import com.sword.signature.api.sign.FLOW_NAME_MIME_TYPE
import com.sword.signature.business.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.MetadataExtractorRegistry
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.metadataToExtract
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Configuration
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
            registry.metadataToExtract<String>(MimeType.valueOf(ALGORITHM_MIME_TYPE), "algorithm")
            registry.metadataToExtract<String>(MimeType.valueOf(FLOW_NAME_MIME_TYPE), "flowName")
        }
    }


    @Bean
    fun payloadSocketAcceptorInterceptor(security: RSocketSecurity, jwtReactiveAuthenticationManager: ReactiveAuthenticationManager): PayloadSocketAcceptorInterceptor {
        return security
                .authorizePayload { spec: AuthorizePayloadsSpec ->
                    spec
                            .route("newJobs").authenticated()
                            .anyExchange().permitAll()
                }
                .jwt { jwtSpec ->
                    jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)
                }
                .build()
    }

    @Bean
    fun jwtReactiveAuthenticationManager(customReactiveJwtDecoder: CustomReactiveJwtDecoder): ReactiveAuthenticationManager {
        /*val jwtReactiveAuthenticationManager = JwtReactiveAuthenticationManager(customReactiveJwtDecoder)

        val authenticationConverter = JwtAuthenticationConverter()
        val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        jwtReactiveAuthenticationManager.setJwtAuthenticationConverter(ReactiveJwtAuthenticationConverterAdapter(authenticationConverter))

        return jwtReactiveAuthenticationManager*/
        return ReactiveAuthenticationManager {
            println(it)
            Mono.just(CustomAuthentication())
        }
    }
}

class CustomAuthentication() : Authentication {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(CustomAuth())
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {

    }

    override fun getName(): String {
        return "super"
    }

    override fun getCredentials(): Any {
        return "password"
    }

    override fun getPrincipal(): Any {
        return "admin"
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun getDetails(): Any? {
        return null
    }

    internal class CustomAuth : GrantedAuthority {
        override fun getAuthority(): String {
            return "ROLE_ADMIN"
        }
    }
}

@Component
class CustomReactiveJwtDecoder(
        private val tokenService: TokenService
) : ReactiveJwtDecoder {

    private val objectMapper = JacksonJsonParser()

    override fun decode(token: String): Mono<Jwt> {
        LOGGER.debug("Token: ", token)
        val decodedJWT: DecodedJWT = tokenService.verifyToken(token)
        val claims = decodedJWT.claims
        val headers = Collections.singletonMap("Content-Type", "application/json") as Map<String, Any>
        return Mono.just(Jwt(token, Instant.now(), Instant.now().plus(10, ChronoUnit.DAYS), headers, claims as Map<String, Any>))
        return Mono.just(decodedJWT.toJwt())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CustomReactiveJwtDecoder::class.java)
    }
}

fun DecodedJWT.toJwt() = Jwt(
        token,
        issuedAt.toInstant(),
        expiresAt.toInstant(),
        null,
        claims as Map<String, Any>?
)
