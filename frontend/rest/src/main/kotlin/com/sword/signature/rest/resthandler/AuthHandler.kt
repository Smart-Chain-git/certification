package com.sword.signature.rest.resthandler

import com.sword.signature.api.account.Account
import com.sword.signature.api.auth.AuthRequest
import com.sword.signature.api.auth.AuthResponse
import com.sword.signature.business.service.AccountService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.authentication.JwtTokenService
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("\${api.base-path:/api}")
class AuthHandler (
    private val accountService: AccountService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val jwtTokenService: JwtTokenService,
    @Value("\${jwt.duration}") private val tokenDuration: Duration
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

        val token = jwtTokenService.generateVolatileToken(user.id,tokenDuration)

        return AuthResponse(token = token)
    }

    @RequestMapping(
        value = ["/me"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun me(
        @AuthenticationPrincipal user: CustomUserDetails
    ): Account {
        return user.account.toWeb()
    }




}