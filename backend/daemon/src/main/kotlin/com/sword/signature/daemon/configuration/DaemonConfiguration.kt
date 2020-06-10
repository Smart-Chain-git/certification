package com.sword.signature.daemon.configuration

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableEncryptableProperties
@ComponentScan(basePackages = ["com.sword.signature"])
class DaemonConfiguration