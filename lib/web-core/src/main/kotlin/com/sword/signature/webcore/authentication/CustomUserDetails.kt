package com.sword.signature.webcore.authentication

import com.sword.signature.business.model.Account
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val account: Account,
    roles: List<String>,
    private val enabled: Boolean = true,
    private val credentialsNonExpired: Boolean = true,
    private val accountNonExpired: Boolean = true,
    private val accountNonLocked: Boolean = true
) : UserDetails {

    private val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()

    override fun getAuthorities() = authorities

    override fun isEnabled() = enabled

    override fun getUsername() = account.login

    override fun isCredentialsNonExpired() = credentialsNonExpired

    override fun getPassword() = account.password

    override fun isAccountNonExpired() = accountNonExpired

    override fun isAccountNonLocked() = accountNonLocked

}