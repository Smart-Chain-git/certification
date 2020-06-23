package com.sword.signature.rest.resthandler

import com.sword.signature.api.account.Account
import com.sword.signature.api.account.AccountCreate
import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.model.AccountValidation
import com.sword.signature.business.model.mail.SignUpMail
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.MailService
import com.sword.signature.rest.checkPassword
import com.sword.signature.webcore.authentication.*
import com.sword.signature.webcore.mapper.toWeb
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.*

@RestController
@RequestMapping("\${api.base-path:/api}")
class AccountHandler(
    val accountService: AccountService,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    val mailService: MailService,
    val jwtTokenService: JwtTokenService
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
        @RequestHeader("origin") origin: String,
        @RequestBody accountDetails: AccountCreate
    ): Account {
        val createdAccount = accountService.createAccount(
            requester = user.account,
            accountDetails = com.sword.signature.business.model.AccountCreate(
                login = accountDetails.login,
                email = accountDetails.email,
                password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString() + OffsetDateTime.now().toString() + UUID.randomUUID().toString()),
                fullName = accountDetails.fullName,
                company = accountDetails.company,
                country = accountDetails.country,
                publicKey = accountDetails.publicKey,
                hash = accountDetails.hash,
                isAdmin = accountDetails.isAdmin
            )
        )

        val token = jwtTokenService.generateVolatileToken(createdAccount.id, java.time.Duration.ofDays(1), createdAccount.password)

        // Send signup email
        mailService.sendEmail(SignUpMail(recipient = createdAccount, link = "$origin/#/activation/$token"))

        return createdAccount.toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
            value = ["/accounts"],
            produces = ["application/json"],
            method = [RequestMethod.GET]
    )
    suspend fun getAccounts(
            @AuthenticationPrincipal user: CustomUserDetails
    ): Flow<Account> {
        return accountService.getAccounts().map { it.toWeb() }
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
            hash = accountDetails.hash,
            disabled = accountDetails.disabled
        )

        val account =
            accountService.patchAccount(requester = user.account, accountId = accountId, accountDetails = accountPatch)
        return account.toWeb()
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/validate-account"],
        produces = ["application/json"],
        method = [RequestMethod.POST]
    )
    suspend fun validate(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestBody accountDetails: AccountValidation
    ): Account {
        val credentials = ReactiveSecurityContextHolder.getContext().map { it.authentication.credentials }.awaitSingle()
        val token = jwtTokenService.parseToken(credentials.toString()) as ActivationToken
        val account = accountService.getAccount(token.id)

        if (account!!.password != token.password) {
            throw AuthenticationException.RevokedTokenException(credentials.toString())
        } else {
            checkPassword(accountDetails.password)
            return accountService.activateAccount(account, bCryptPasswordEncoder.encode(accountDetails.password)).toWeb()
        }
    }

    @Operation(security = [SecurityRequirement(name = "bearer-key")])
    @RequestMapping(
        value = ["/validate-account"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    suspend fun getAccountByActivationToken(): Account {
        val credentials = ReactiveSecurityContextHolder.getContext().map { it.authentication.credentials }.awaitSingle()
        val token = jwtTokenService.parseToken(credentials.toString()) as ActivationToken

        val account = accountService.getAccount(token.id)
        if (account!!.password != token.password) {
            throw AuthenticationException.RevokedTokenException(token.toString())
        } else {
            return account.toWeb()
        }
    }

}

