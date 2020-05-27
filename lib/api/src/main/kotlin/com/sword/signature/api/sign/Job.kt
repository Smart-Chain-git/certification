package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Job(
    val id: String,
    val createdDate: OffsetDateTime,
    val injectedDate: OffsetDateTime?,
    val validatedDate: OffsetDateTime?,
    val numberOfTry: Int = 0,
    val blockHash: String?,
    val blockDepth: Long?,
    val algorithm: String,
    val flowName: String,
    var stateDate: OffsetDateTime,
    var state: String,
    val contractAddress: String?,
    val transactionHash: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JobFile(
    val id: String,
    val hash: String,
    val jobId: String,
    val metadata: SignMetadata,
    val proof: Proof?
)