package com.sword.signature.ihm.configuration

import com.sword.signature.business.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class DatabaseAuthenticationProvider(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val accountService: AccountService
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {

        val name = authentication.name
        val password = authentication.credentials.toString()

        val account = accountService.getAccountByLoginOrEmail(name)

        if (!bCryptPasswordEncoder.matches(password, account?.password)) {
            LOGGER.error("Bad credential for {}", name)
            throw AuthenticationServiceException("Bad credential for $name")
        }

        // Authentification reussi.
        LOGGER.info("User {} is authenticated", name)
        return UsernamePasswordAuthenticationToken(name, password, listOf(SimpleGrantedAuthority("ADMIN")))

    }


    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}