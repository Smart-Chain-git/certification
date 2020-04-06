package com.sword.signature.business.exception

import com.sword.signature.business.model.Token

sealed class AuthenticationException(override val message: String) : UserServiceException(message) {

    class RevokedTokenException(jwtToken: String) : AuthenticationException("Token '$jwtToken' does not exist or has benn revoked.")
    class ExpiredTokenException(token: Token) : AuthenticationException("Token '${token.jwtToken}' expired since ${token.expirationDate}.")
}