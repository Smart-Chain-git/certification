package com.sword.signature.daemon.configuration

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Configuration
@ComponentScan(basePackages = ["com.sword.signature"])
class DaemonConfiguration {

    companion object {
        private val logger = LoggerFactory.getLogger(DaemonConfiguration::class.java)
    }

}