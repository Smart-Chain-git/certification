package com.sword.signature.ihm.configuration


import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke

@EnableWebSecurity
@Configuration
class KotlinSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http {
            cors { disable() }
            csrf { disable() }
            authorizeRequests {

                authorize("/css/", permitAll)
                authorize("/webjars/", permitAll)
                authorize("/images/", permitAll)
                authorize("/index", permitAll)
                authorize("/login", permitAll)

            }
            exceptionHandling {
                accessDeniedPage = "/forbidden"
            }

            formLogin {
                loginPage = "/login"
                failureUrl = "/loginerror"
            }
        }
    }
}


