package com.sword.signature.ihm.configuration


import com.sword.signature.ihm.webhandler.MainHandler
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.server.router

fun routes(mainHandler: MainHandler) = router {
    accept(TEXT_HTML).nest {
        GET("/", mainHandler::index)
        GET("/login", mainHandler::login)
    }
    resources("/**", ClassPathResource("/static"))
}


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





