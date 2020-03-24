package com.sword.signature.ihm.configuration


import com.sword.signature.ihm.webhandler.MainHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect


fun bean() = beans {
    bean {
        //encodage des mot de passe
        BCryptPasswordEncoder()
    }
    bean {
        UserDetailsService(ref(), ref())
    }
    bean {
        MainHandler()
    }
    bean {
        routes(ref())
    }
    bean {
        securityWebFilterChain(ref())
    }
    bean {
        //support spring security for thymeleaf
        SpringSecurityDialect()
    }
}

@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.sword.signature"])
class BeansInitialiser : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        bean().initialize(context = applicationContext)
    }
}