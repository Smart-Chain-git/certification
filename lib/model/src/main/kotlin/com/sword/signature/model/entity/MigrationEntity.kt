package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "migrations")
data class MigrationEntity(
        @Id
        @Field(value = "_id")
        val id: String? = null,
        @Indexed
        val name: String,
        val hash: String
) {

}
