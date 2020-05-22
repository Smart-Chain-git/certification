package com.sword.signature.daemon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder

inline fun <reified R : Any> R.logger(): Logger =
    LoggerFactory.getLogger(this::class.java.name.substringBefore("\$Companion"))


fun MessageChannel.sendPayload(payload: Any) {
    send(MessageBuilder.withPayload(payload).build())
}