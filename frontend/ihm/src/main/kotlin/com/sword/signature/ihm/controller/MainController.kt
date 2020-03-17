package com.sword.signature.ihm.controller

import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class MainHandler {


    fun index(request: ServerRequest): ServerResponse {
        val model = mapOf<String, String>()
        return ServerResponse.ok().render("index", model)
    }

    fun login(request: ServerRequest): ServerResponse {
        logger.debug("coucou request:{}",request.params())
        return ServerResponse.ok().render("login")
    }


    fun loginError(request: ServerRequest): ServerResponse {
        val model =request.params()
        model["loginError"]= "true"
        return ServerResponse.ok().render("login",model)
    }

    companion object {
        val logger = LoggerFactory.getLogger(MainHandler::class.java)
    }

}