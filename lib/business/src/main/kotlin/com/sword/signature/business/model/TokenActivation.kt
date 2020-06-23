package com.sword.signature.business.model


data class TokenActivation(
    val id: String,
    val login: String,
    val password: String
) {
}
