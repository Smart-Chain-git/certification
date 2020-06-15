package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.Token
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.TokenCreateRequest
import com.sword.signature.business.model.TokenPatch
import com.sword.signature.business.service.TokenService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.authentication.JwtTokenService
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDate

@RestController
@RequestMapping("\${api.base-path:/api}")
class TokenHandler(
        private val tokenService: TokenService,
        private val jwtTokenService: JwtTokenService
) {
    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/tokens"],
            produces = ["application/json"],
            method = [RequestMethod.GET]
    )
    suspend fun token(
            @AuthenticationPrincipal user: CustomUserDetails
    ): Flow<Token> {
        return tokenService.findAll(requester = user.account).map { it.toWeb() }
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/tokens/{tokenId}"],
            produces = ["application/json"],
            method = [RequestMethod.PATCH]
    )
    suspend fun patchToken(
            @AuthenticationPrincipal user: CustomUserDetails,
            @Parameter(description = "token Id") @PathVariable(value = "tokenId") tokenId: String,
            @RequestBody tokenPatch: TokenPatch
    ): Token {
        return tokenService.patchToken(requester = user.account, tokenId = tokenId, tokenDetails = tokenPatch).toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/tokens"],
            produces = ["application/json"],
            method = [RequestMethod.POST]
    )
    suspend fun createToken(
            @AuthenticationPrincipal user: CustomUserDetails,
            @RequestBody tokenCreate: TokenCreateRequest
    ) : Token {
        val now = LocalDate.now()
        val jwtToken = jwtTokenService.generateVolatileToken(user.account.id, Duration.between(now.atStartOfDay(), tokenCreate.expirationDate?.atStartOfDay()))
        val token = TokenCreate(
                name = tokenCreate.name,
                expirationDate = tokenCreate.expirationDate,
                jwtToken = jwtToken,
                accountId = user.account.id
        )
        return tokenService.createToken(token).toWeb()
    }

}
