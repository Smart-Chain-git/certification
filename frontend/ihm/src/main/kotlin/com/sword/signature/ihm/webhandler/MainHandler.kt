package com.sword.signature.ihm.webhandler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait
import reactor.core.publisher.Mono


class MainHandler {


    suspend fun index(request: ServerRequest): ServerResponse {
        val model = mapOf<String, String>()
        return ServerResponse.ok().html().renderAndAwait("index", model)
    }

    suspend fun login(request: ServerRequest): ServerResponse {
        val model = mutableMapOf<String, String>()
        if( request.queryParam("error").isPresent){
            model["loginError"] = "true"
        }
        return ServerResponse.ok().html().renderAndAwait("login",model)
    }

}