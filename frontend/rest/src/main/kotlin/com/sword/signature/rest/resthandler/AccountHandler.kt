package com.sword.signature.rest.resthandler

import com.sword.signature.api.sign.Account
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.PasswordTooWeakException
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.service.AccountService
import com.sword.signature.rest.PasswordChecker
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:/api}")
class AccountHandler(
        val accountService: AccountService,
        val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/accounts/{accountId}"],
            produces = ["application/json"],
            method = [RequestMethod.GET]
    )
    suspend fun account(
            @AuthenticationPrincipal user: CustomUserDetails,
            @Parameter(description = "account Id") @PathVariable(value = "accountId") accountId: String
    ): Account {
        val account =
                accountService.getAccount(accountId) ?: throw EntityNotFoundException("account", accountId)
        return account.toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/accounts/{accountId}"],
            produces = ["application/json"],
            method = [RequestMethod.PATCH]
    )
    suspend fun patchAccount(
            @AuthenticationPrincipal user: CustomUserDetails,
            @Parameter(description = "account Id") @PathVariable(value = "accountId") accountId: String,
            @RequestBody accountDetails: AccountPatch
    ): Account {
        val accountPatch = AccountPatch(
                login = accountDetails.login,
                email = accountDetails.email,
                isAdmin = accountDetails.isAdmin,
                fullName = accountDetails.fullName,
                password = if (accountDetails.password != null) {
                                PasswordChecker.checkPassword(accountDetails.password.toString())
                                bCryptPasswordEncoder.encode(accountDetails.password)
                            } else {
                                null
                            }
        )

        val account =
                accountService.patchAccount(accountId, accountPatch)
        return account.toWeb()
    }
}

