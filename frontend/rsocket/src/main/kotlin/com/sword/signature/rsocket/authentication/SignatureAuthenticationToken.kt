package com.sword.signature.web.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SignatureAuthenticationToken(
        private val principal: UserDetails,
        private val credentials: Any,
        authorities: MutableCollection<out GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any {
        return credentials
    }

    override fun getPrincipal(): Any {
        return principal
    }

    override fun isAuthenticated(): Boolean {
        return true
    }
}