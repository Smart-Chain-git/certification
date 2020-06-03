package com.sword.signature.api.sign

data class AuthRequest(
    val user: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class  Account (
    val id: String,
    val login: String,
    val email: String,
    val fullName: String?,
    val isAdmin: Boolean,
    val pubKey: String?
)
