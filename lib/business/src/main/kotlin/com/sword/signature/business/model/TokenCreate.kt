package com.sword.signature.business.model

import java.time.LocalDate

data class TokenCreate(
        val name: String,
        val jwtToken: String,
        val expirationDate: LocalDate? = null,
        val creationDate: LocalDate,
        val accountId: String
) {
}
