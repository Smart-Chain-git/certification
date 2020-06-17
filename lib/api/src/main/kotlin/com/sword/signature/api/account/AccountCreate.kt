package com.sword.signature.api.account

data class AccountCreate(
    val login: String,
    val email: String,
    val fullName: String?,
    val company: String?,
    val country: String?,
    val publicKey: String?,
    val hash: String?,
    val isAdmin: Boolean = false
)
