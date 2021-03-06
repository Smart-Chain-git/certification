package com.sword.signature.webcore.authentication

import com.sword.signature.business.model.Account
import com.sword.signature.business.service.AccountService
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(
    private val accountService: AccountService
) : ReactiveUserDetailsService {

    suspend fun findById(userId: String): UserDetails {
        val account =
            accountService.getAccount(userId) ?: throw UsernameNotFoundException("Account with id '$userId' not found.")

        return buildUser(account)
    }

    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            val account = accountService.getAccountByLoginOrEmail(username)
                ?: throw UsernameNotFoundException("Account with username '$username' not found.")
            buildUser(account)
        }
    }

    fun buildUser(account: Account): UserDetails {
        // Check whether the user is disabled or not.
        if (account.disabled) {
            throw DisabledException("Account is disabled")
        }

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
