package com.sword.signature.rest.resthandler

import com.sword.signature.api.token.Token
import com.sword.signature.api.token.TokenCreate
import com.sword.signature.business.model.TokenPatch
import com.sword.signature.business.service.TokenService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.authentication.JwtTokenService
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
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
        consumes = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun createToken(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestBody tokenDetails: TokenCreate
    ): Token {
        val accountId = user.account.id

        return tokenService.createToken(
            com.sword.signature.business.model.TokenCreate(
                name = tokenDetails.name,
                jwtToken = jwtTokenService.generatePersistantToken(accountId),
                expirationDate = tokenDetails.expirationDate,
                accountId = accountId
            )
        ).toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/tokens"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getTokens(
        @AuthenticationPrincipal user: CustomUserDetails
    ): Flow<Token> {
        return tokenService.findAll(requester = user.account).map { it.toWeb() }
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/tokens/{tokenId}/revoke"],
        produces = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun revokeToken(
        @AuthenticationPrincipal user: CustomUserDetails,
        @PathVariable("tokenId") tokenId: String
    ): Token {
        return tokenService.patchToken(
            requester = user.account,
            tokenId = tokenId,
            tokenDetails = TokenPatch(revoked = true)
        ).toWeb()
    }
}
