package com.sword.signature.daemon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DaemonApplication



fun main(args: Array<String>) {
    runApplication<DaemonApplication>(*args)
}