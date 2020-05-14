package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.AuthRequest
import com.sword.signature.api.sign.AuthResponse
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:/api}")
class AuthHandler {

    @RequestMapping(
        value = ["/auth"],
        consumes = ["application/json"],
        produces = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun auth(
        @Parameter(
            description = "request of autentification",
            required = true
        )@RequestBody request: AuthRequest
    ): AuthResponse {

        return AuthResponse(token = "suptoken")
    }


}