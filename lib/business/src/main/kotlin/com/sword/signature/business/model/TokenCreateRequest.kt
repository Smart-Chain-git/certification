package com.sword.signature.business.model

import java.time.LocalDate

data class TokenCreateRequest(
        val name: String,
        val expirationDate: LocalDate? = null
) {
}
