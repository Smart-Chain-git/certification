package com.sword.signature.business.model

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class TokenCreate(
        val name: String,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        val expirationDate: LocalDate? = null,
        val accountId: String
) {
}