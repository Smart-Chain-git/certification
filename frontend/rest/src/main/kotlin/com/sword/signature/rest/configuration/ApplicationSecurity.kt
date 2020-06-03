package com.sword.signature.rest.configuration


import com.sword.signature.rest.authentication.SecurityContextRepository
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.sword.signature"])
class ApplicationSecurity {


    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        securityContextRepository: SecurityContextRepository
    ): SecurityWebFilterChain {
        http.csrf().disable()
        http.httpBasic().disable()
        http.securityContextRepository(securityContextRepository)
        http.authorizeExchange { exchanges ->
            // Authorize every OPTIONS request for browser verification purpose.
            exchanges.pathMatchers(HttpMethod.OPTIONS,"/**").permitAll()
            // Authorize the login endpoint to be accessed without authentication.
            exchanges.pathMatchers(HttpMethod.POST, "/api/auth").permitAll()
            // Require an authentication for all API request apart from the login.
            exchanges.pathMatchers("/api/**").authenticated()
            // Authorize all other requests (client, SwaggerUI).
            exchanges.anyExchange().permitAll()
        }

        return http.build()
    }

    @Bean
    fun corsFilter(): CorsWebFilter {

        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOrigin("http://localhost:8082")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsWebFilter(source)
    }


    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-key",
                        SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    )
            )
    }

}