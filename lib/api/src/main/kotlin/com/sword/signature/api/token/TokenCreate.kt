package com.sword.signature.api.token

import java.time.LocalDate

data class TokenCreate(
    val name: String,
    val expirationDate: LocalDate?
)
