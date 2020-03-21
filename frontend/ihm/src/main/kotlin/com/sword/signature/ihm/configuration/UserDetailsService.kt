package com.sword.signature.ihm.configuration

import com.sword.signature.business.service.AccountService
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Mono


class UserDetailsService(
    private val accountService: AccountService
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            val account = accountService.getAccountByLoginOrEmail(username)?: throw UsernameNotFoundException("account $username not found")
            User.builder()
                .username(username)
                .password(account?.password)
                .roles("Admin")
                .build()
        }
    }

}