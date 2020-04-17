package com.sword.signature.business.model

import java.time.LocalDate

data class TokenCreate(
        val name: String,
        val expirationDate: LocalDate? = null,
        val accountId: String
) {
}