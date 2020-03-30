package com.sword.signature.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "nodes")
data class NodeEntity(
    @Id
    @Field(value = "_id")
    val id: String? = null,

    @Indexed(unique = true)
    val hash: String,

    val fileName: String,
    @Indexed
    val parentId: String?,
    val position: NodePosition?,
    val jobId: String,
    val type: NodeType
)

enum class NodeType(var shorthand: String) {
    NODE("N"),
    LEAF("L")
}

enum class NodePosition(var shorthand: String) {
    LEFT("L"),
    RIGHT("R")
}