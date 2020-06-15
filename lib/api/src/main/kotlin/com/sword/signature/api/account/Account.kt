package com.sword.signature.api.account

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Account(
    val id: String,
    val login: String,
    val email: String,
    val fullName: String?,
    val isAdmin: Boolean,
    val pubKey: String?
)
