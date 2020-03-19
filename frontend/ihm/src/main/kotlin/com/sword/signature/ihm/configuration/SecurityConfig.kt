package com.sword.signature.ihm.configuration


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke

@EnableWebSecurity
@Configuration
class KotlinSecurityConfiguration(
    private val databaseAuthenticationProvider :DatabaseAuthenticationProvider
) : WebSecurityConfigurerAdapter() {

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

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(databaseAuthenticationProvider)
    }
}


