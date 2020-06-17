package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "tokens")
data class TokenEntity(
    @Id
    val id: String? = null,
    @Indexed(direction = IndexDirection.ASCENDING)
    val name: String,
    @Indexed(unique = true)
    val jwtToken: String,
    val expirationDate: LocalDate? = null,
    val creationDate: LocalDate,
    val accountId: String,
    val revoked: Boolean = false
) {
}
