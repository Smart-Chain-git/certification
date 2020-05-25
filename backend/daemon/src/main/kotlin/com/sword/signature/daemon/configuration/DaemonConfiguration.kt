package com.sword.signature.daemon.configuration


import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Configuration
@ComponentScan(basePackages = ["com.sword.signature"])
class DaemonConfiguration