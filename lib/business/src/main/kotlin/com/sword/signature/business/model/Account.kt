package com.sword.signature.business.model

import java.io.Serializable

data class Account(
    val id: String,
    val login: String,
    val email: String,
    val password: String,
    val fullName: String?,
    val company: String?,
    val country: String?,
    val publicKey: String?,
    val hash: String?,
    val isAdmin: Boolean,
    val disabled: Boolean
) : Serializable
