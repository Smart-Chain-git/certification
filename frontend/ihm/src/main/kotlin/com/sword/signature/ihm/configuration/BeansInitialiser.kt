package com.sword.signature.ihm.configuration

import com.sword.signature.ihm.controller.MainHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.servlet.function.router


fun bean() = beans {
    bean {

        fun user(user: String, pw: String, vararg roles: String) =
            User.withDefaultPasswordEncoder().username(user).password(pw).roles(*roles).build()

        InMemoryUserDetailsManager(user("user", "user", "USER"), user("admin", "admin", "USER", "ADMIN"))
    }
    bean {
        MainHandler()
    }
    bean {
        router {
            accept(TEXT_HTML).nest {
                GET("/", ref<MainHandler>()::index)
                GET("/login", ref<MainHandler>()::login)
                GET("/loginerror", ref<MainHandler>()::loginError)
            }
            resources("/**", ClassPathResource("static/"))
        }
    }

}


class BeansInitialiser : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        bean().initialize(context = applicationContext)
    }
}