package com.sword.signature.web.rsocketcontroler

import com.sword.signature.greeting.GreetingRequest
import com.sword.signature.greeting.GreetingResponse
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.time.Instant


@Controller
internal class GreetingController {

    @MessageMapping("greetings")
    suspend fun greet(@AuthenticationPrincipal user: String, request: GreetingRequest): GreetingResponse {
        LOGGER.debug("recu de {} : {}", user, request)
        return GreetingResponse("Hello " + user + " @ " + Instant.now().toString())
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(GreetingController::class.java)
    }

}