package com.sword.signature.ui.configuration

import com.sword.signature.ui.webhandler.JobHandler
import com.sword.signature.ui.webhandler.MainHandler
import com.sword.signature.ui.webhandler.TokenHandler
import nz.net.ultraq.thymeleaf.LayoutDialect
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
class ApplicationSecurity {


    /**
     * Spring security support for thymeleaf.
     */
    @Bean
    fun springSecurityDialect() = SpringSecurityDialect()

    @Bean
    fun layoutDialect() = LayoutDialect()


    @Bean
    fun routes(
        mainHandler: MainHandler,
        jobHandler: JobHandler,
        tokenHandler: TokenHandler
    ) = coRouter {
        accept(MediaType.TEXT_HTML).nest {
            GET("/", mainHandler::index)
            GET("/index", mainHandler::index)
            GET("/login", mainHandler::login)
            GET("/jobs", jobHandler::jobs)
            GET("/jobs/{jobId}", jobHandler::job)
            GET("/files/{fileId}/proof", jobHandler::fileProof)
            GET("/tokens", tokenHandler::tokens)
            POST("/createToken", tokenHandler::addToken)
            GET("/revokeToken/{id}", tokenHandler::revokeToken)
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