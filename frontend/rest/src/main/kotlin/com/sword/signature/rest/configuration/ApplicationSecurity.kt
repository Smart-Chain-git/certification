package com.sword.signature.rest.configuration



import com.sword.signature.rest.authentication.SecurityContextRepository
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
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
            exchanges.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            exchanges.anyExchange().authenticated()
        }

        return http.build()
    }
}