package com.sword.signature.ihm.configuration

import com.sword.signature.business.service.AccountService
import com.sword.signature.ihm.controller.MainHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.function.router
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect


fun bean() = beans {
    bean {
        BCryptPasswordEncoder()
    }
    bean {
        DatabaseAuthenticationProvider(ref(),ref())
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
            resources("/css", ClassPathResource("/static/css"))
        }
    }
    bean {
        //support spring security for thymeleaf
        SpringSecurityDialect()
    }

}


class BeansInitialiser : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        bean().initialize(context = applicationContext)
    }
}