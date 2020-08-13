package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "algorithms")
class AlgorithmEntity(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val name: String,
    val digestLength: Int
)
