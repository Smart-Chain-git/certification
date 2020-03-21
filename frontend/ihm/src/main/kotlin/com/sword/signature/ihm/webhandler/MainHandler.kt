package com.sword.signature.ihm.webhandler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import reactor.core.publisher.Mono


class MainHandler {


    fun index(request: ServerRequest): Mono<ServerResponse> {
        val model = mapOf<String, String>()
        return ServerResponse.ok().html().render("index", model)
    }

    fun login(request: ServerRequest): Mono<ServerResponse> {
        val model = mutableMapOf<String, String>()
        if( request.queryParam("error").isPresent){
            model["loginError"] = "true"
        }
        return ServerResponse.ok().html().render("login",model)
    }

}