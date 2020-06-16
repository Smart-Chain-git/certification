package com.sword.signature.business.model

data class AccountCreate(
    val login: String,
    val email: String,
    val password: String,
    val fullName: String?,
    val company: String?,
    val country: String?,
    val publicKey: String?,
    val hash: String?,
    val isAdmin: Boolean = false,
    val disabled: Boolean = false
)
