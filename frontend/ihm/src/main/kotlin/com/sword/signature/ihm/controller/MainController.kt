package com.sword.signature.ihm.controller

import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class MainHandler {


    fun index(request: ServerRequest): ServerResponse {
        val model = mapOf<String, String>()
        return ServerResponse.ok().render("index", model)
    }

    fun login(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().render("login")
    }

    fun loginError(request: ServerRequest): ServerResponse {
        val model = mapOf("loginError" to "true")
        return ServerResponse.ok().render("login",model)
    }

}