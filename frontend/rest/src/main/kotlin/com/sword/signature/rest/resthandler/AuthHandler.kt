package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.AuthRequest
import com.sword.signature.api.sign.AuthResponse
import com.sword.signature.business.service.AccountService
import com.sword.signature.webcore.authentication.UserDetailsService
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:/api}")
class AuthHandler (
    private val accountService: AccountService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
){

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
        val user = accountService.getAccountByLoginOrEmail(request.user)?:throw BadCredentialsException("Invalid credentials")

        if (!bCryptPasswordEncoder.matches(request.password , user.password)) {
            throw BadCredentialsException("Invalid credentials")
        }

        return AuthResponse(token = "suptoken")
    }


}