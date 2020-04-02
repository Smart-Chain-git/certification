package com.sword.signature.business.model

data class AccountPatch(
    val login: String? = null,
    val email: String? = null,
    val password: String? = null,
    val fullName: String? = null
)
