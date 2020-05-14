package com.sword.signature.rest.configuration



import com.sword.signature.rest.authentication.SecurityContextRepository
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.sword.signature"])
class ApplicationSecurity {



    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity,securityContextRepository: SecurityContextRepository): SecurityWebFilterChain {
        http.cors().disable()
        http.csrf().disable()
        http.securityContextRepository(securityContextRepository)
        http.authorizeExchange { exchanges ->
            exchanges.pathMatchers(
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/api/auth"
            ).permitAll()
            exchanges.anyExchange().authenticated()
        }

        return http.build()
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