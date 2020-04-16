package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "accounts")
data class AccountEntity(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val login: String,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val fullName: String?,
    val isAdmin: Boolean = false
) {

}
