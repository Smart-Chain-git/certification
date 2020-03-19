package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "accounts")
data class AccountEntity(
        @Id
        @Field(value="_id")
        val id: String? = null,
        @Indexed(unique = true)
        val login: String,
        @Indexed(unique = true)
        val email: String,
        val password: String,
        val fullName: String?
) {

}
