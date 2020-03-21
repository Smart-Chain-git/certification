package com.sword.signature.ihm.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.sword.signature"])
class ScopeConfiguration {

}
