package com.sword.signature.business.model

data class AccountPatch(
    val login: String? = null,
    val email: String? = null,
    val password: String? = null,
    val fullName: String? = null,
    val company: String? = null,
    val country: String? = null,
    val publicKey: String? = null,
    val hash: String? = null,
    val isAdmin: Boolean? = null,
    val isActive: Boolean? = null
)
