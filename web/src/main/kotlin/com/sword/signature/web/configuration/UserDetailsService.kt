package com.sword.signature.web.configuration

import com.sword.signature.business.model.mail.HelloAccountMail
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.MailService
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Mono


class UserDetailsService(
        private val accountService: AccountService,
        private val mailService: MailService
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            val account = accountService.getAccountByLoginOrEmail(username)
                    ?: throw UsernameNotFoundException("account $username not found")
            mailService.sendEmail(HelloAccountMail(account))
            User.builder()
                    .username(username)
                    .password(account.password)
                    .roles("Admin")
                    .build()
        }
    }
}