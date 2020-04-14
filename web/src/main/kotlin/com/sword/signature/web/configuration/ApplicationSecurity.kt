package com.sword.signature.web.configuration

import com.sword.signature.web.webhandler.JobHandler
import com.sword.signature.web.webhandler.MainHandler
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.server.coRouter
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect

@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.sword.signature"])
class ApplicationSecurity{

    /**
     * Password encoding.
     */
    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()


    /**
     * Spring security support for thymeleaf.
     */
    @Bean
    fun springSecurityDialect() = SpringSecurityDialect()


    @Bean
    fun routes(
        mainHandler: MainHandler,
        jobHandler: JobHandler
    ) = coRouter {
        accept(MediaType.TEXT_HTML).nest {
            GET("/", mainHandler::index)
            GET("/login", mainHandler::login)
            GET("/jobs", jobHandler::jobs)
        }
        resources("/**", ClassPathResource("/static"))
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.cors().disable()
        http.csrf().disable()
        http.authorizeExchange { exchanges ->
            exchanges.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            exchanges.pathMatchers("/favicon.ico", "/webjars/**", "/login").permitAll()
            exchanges.anyExchange().authenticated()
        }
        http.formLogin().loginPage("/login")

        return http.build()
    }
}