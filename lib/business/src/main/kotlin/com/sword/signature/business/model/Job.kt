package com.sword.signature.business.model

import java.time.LocalDateTime

data class Job(
    val id: String,

    /**
     * Date of the request
     */
    val createdDate: LocalDateTime = LocalDateTime.now(),

    /**
     * data of the last injection in the block chain
     */
    val injectedDate: LocalDateTime? = null,
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
    val userId: String,

    val files: List<String>? = null
)