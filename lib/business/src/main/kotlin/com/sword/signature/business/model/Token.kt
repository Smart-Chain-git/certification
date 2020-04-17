package com.sword.signature.business.model

import java.time.LocalDate

data class Token(
        val id: String,
        val name: String,
        val jwtToken: String,
        val expirationDate: LocalDate? = null,
        val accountId: String,
        val revoked: Boolean
) {
}