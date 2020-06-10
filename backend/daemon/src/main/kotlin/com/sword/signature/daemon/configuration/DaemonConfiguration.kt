package com.sword.signature.daemon.configuration

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableEncryptableProperties
@EnableConfigurationProperties
@ComponentScan(basePackages = ["com.sword.signature"])
class DaemonConfiguration