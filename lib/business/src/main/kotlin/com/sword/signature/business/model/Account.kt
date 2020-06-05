package com.sword.signature.business.model

import java.io.Serializable

data class Account(
    val id: String,
    val login: String,
    val email: String,
    val password: String,
    val fullName: String?,
    val isAdmin: Boolean,
    val pubKey: String?
):Serializable
