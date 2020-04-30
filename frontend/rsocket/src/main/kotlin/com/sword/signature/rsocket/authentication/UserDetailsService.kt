package com.sword.signature.rsocket.authentication

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.mail.HelloAccountMail
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.MailService
import com.sword.signature.rsocket.authentication.CustomUserDetails
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(
    private val accountService: AccountService,
    private val mailService: MailService
) : ReactiveUserDetailsService {

    fun findById(userId: String): UserDetails {
        val account = runBlocking {
            accountService.getAccount(userId) ?: throw UsernameNotFoundException("Account with id '$userId' not found.")
        }
        return buildUser(account)
    }

    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            val account = accountService.getAccountByLoginOrEmail(username)
                ?: throw UsernameNotFoundException("Account with username '$username' not found.")
            mailService.sendEmail(HelloAccountMail(account))
            buildUser(account)
        }
    }

    fun buildUser(account: Account): UserDetails {
        val roles = mutableListOf("SETUP")
        if (account.isAdmin) {
            roles.add("ADMIN")
        }
        return CustomUserDetails(
            account = account,
            roles = roles
        )
    }
}

