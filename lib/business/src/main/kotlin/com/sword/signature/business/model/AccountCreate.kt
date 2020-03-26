package com.sword.signature.business.model

class AccountCreate(
    val login: String,
    val email: String,
    val password: String,
    val fullName: String?
)