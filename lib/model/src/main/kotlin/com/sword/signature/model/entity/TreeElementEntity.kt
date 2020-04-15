package com.sword.signature.model.entity

import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "treeElements")
data class TreeElementEntity(
    @Id
    @Field(value = "_id")
    val id: String? = null,

    @Indexed(unique = true)
    val hash: String,

    val fileName: String? = null,
    @Indexed
    val parentId: String? = null,
    val position: TreeElementPosition? = null,
    val jobId: String,
    val type: TreeElementType
)
