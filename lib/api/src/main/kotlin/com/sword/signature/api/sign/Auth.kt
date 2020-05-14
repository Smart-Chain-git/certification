package com.sword.signature.api.sign

data class AuthRequest(
    val user: String,
    val password: String
)

data class AuthResponse(
    val token: String
)