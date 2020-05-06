package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Job(
    val id: String,
    val createdDate: OffsetDateTime,
    val injectedDate: OffsetDateTime? = null,
    val validatedDate: OffsetDateTime? = null,
    val numbreOfTry: Int = 0,
    val blockId: String? = null,
    val blockDepth: Int? = null,
    val algorithm: String,
    val flowName: String,
    var stateDate: OffsetDateTime,
    var state: String
    )