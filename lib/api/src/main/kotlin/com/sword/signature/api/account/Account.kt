package com.sword.signature.api.account

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Account(
    val id: String,
    val login: String,
    val email: String,
    val fullName: String?,
    val company: String?,
    val country: String?,
    val publicKey: String?,
    val hash: String?,
    val isAdmin: Boolean,
    val disabled: Boolean
)
