package com.sword.signature.rest.resthandler

import com.sword.signature.api.account.Account
import com.sword.signature.api.account.AccountCreate
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.model.mail.SignUpMail
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.MailService
import com.sword.signature.rest.checkPassword
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
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    val mailService: MailService
) {
    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/accounts"],
        produces = ["application/json"],
        consumes = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun createAccount(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestBody accountDetails: AccountCreate
    ): Account {
        val rawPassword = accountDetails.password
        val createdAccount = accountService.createAccount(
            requester = user.account,
            accountDetails = com.sword.signature.business.model.AccountCreate(
                login = accountDetails.login,
                email = accountDetails.email,
                password = accountDetails.password.let {
                    checkPassword(it)
                    bCryptPasswordEncoder.encode(it)
                },
                fullName = accountDetails.fullName,
                company = accountDetails.company,
                country = accountDetails.country,
                publicKey = accountDetails.publicKey,
                hash = accountDetails.hash,
                isAdmin = accountDetails.isAdmin
            )
        )
        // Send signup email
        mailService.sendEmail(SignUpMail(recipient = createdAccount, rawPassword = rawPassword))

        return createdAccount.toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/accounts/{accountId}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getAccount(
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
            password = accountDetails.password?.let {
                checkPassword(it)
                bCryptPasswordEncoder.encode(it)
            },
            company = accountDetails.company,
            country = accountDetails.country,
            publicKey = accountDetails.publicKey,
            hash = accountDetails.hash
        )

        val account =
            accountService.patchAccount(requester = user.account, accountId = accountId, accountDetails = accountPatch)
        return account.toWeb()
    }
}

