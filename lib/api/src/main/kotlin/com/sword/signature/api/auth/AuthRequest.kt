package com.sword.signature.api.auth

data class AuthRequest(
    val user: String,
    val password: String
)
