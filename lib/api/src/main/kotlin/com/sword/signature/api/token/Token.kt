package com.sword.signature.api.token

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Token(
    val id: String,
    val name: String,
    val jwtToken: String,
    val expirationDate: LocalDate?,
    val creationDate: LocalDate,
    val accountId: String,
    val revoked: Boolean
)
