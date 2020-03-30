package com.sword.signature.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime


@Document(collection = "jobs")
data class JobEntity(
    @Id
    @Field(value = "_id")
    val id: String? = null,

    /**
     * Date of the request
     */
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now(),
    /**
     * data of the last injection in the block chain
     */
    val injectedData: LocalDateTime? = null,
    /**
     * date of the validation in the block chain
     */
    val validatedDate: LocalDateTime? = null,

    val numbreOfTry: Int = 0,
    val blockId: String? = null,
    /**
     * depth of the block at validation date
     */
    val blockDepth: Int? = null,

    /**
     * Algotithm used to build the Merkel tree
     */
    val algorithm: String,

    /**
     * user asking for the signature
     */
    @Indexed
    val userId: String
)