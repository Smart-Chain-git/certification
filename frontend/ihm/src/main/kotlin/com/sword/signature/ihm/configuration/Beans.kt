package com.sword.signature.ihm.configuration

import com.sword.signature.ihm.controller.MainHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.net.URI


val mainHandler = MainHandler()

fun bean() = beans {
    bean {

        fun user(user: String, pw: String, vararg roles: String) =
            User.withDefaultPasswordEncoder().username(user).password(pw).roles(*roles).build()

        InMemoryUserDetailsManager(user("user", "user", "USER"), user("admin", "admin", "USER", "ADMIN"))
    }
    bean {
        router {
            GET("/",mainHandler::index)
            GET("/login",mainHandler::login)
            GET("/loginerror", mainHandler::loginError)
        }
    }

}


class BeansInitialiser : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        bean().initialize(context = applicationContext)
    }
}