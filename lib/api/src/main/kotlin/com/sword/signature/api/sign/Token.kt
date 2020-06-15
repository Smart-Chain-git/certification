package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Token(
        val id: String,
        val name: String,
        val revoked: Boolean,
        val expirationDate: LocalDate?
)
