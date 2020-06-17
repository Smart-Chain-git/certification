package com.sword.signature.daemon.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tezos")
data class TezosConfig(
    val keys: Map<String, Map<String, String>> = mutableMapOf()
) {
}
