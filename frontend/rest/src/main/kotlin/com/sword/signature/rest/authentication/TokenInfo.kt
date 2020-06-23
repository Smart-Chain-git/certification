package com.sword.signature.rest.authentication

sealed class TokenInfo {
    abstract val id: String
    abstract val login: String
}

data class LoginTokenInfo(override val id: String, override val login: String) : TokenInfo()

data class ActivationTokenInfo(override val id: String, override val login: String,
                               val oldPasswordCipher: String) : TokenInfo()
