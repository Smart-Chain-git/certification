package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@Document(collection = "tokens")
data class TokenEntity(
        @Id
        @Field("_id")
        val id: String? = null,
        @Indexed(unique = true)
        val jwtToken: String,
        val expirationDate: LocalDate? = null,
        val accountId: String
) {
}