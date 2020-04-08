package com.sword.signature.web.webhandler

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.renderAndAwait

@Component
class JobHandler {


    suspend fun jobs(request: ServerRequest): ServerResponse {
        val model = mapOf<String, String>()
        return ServerResponse.ok().html().renderAndAwait("jobs/jobs", model)
    }



}