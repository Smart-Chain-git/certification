package com.sword.signature.business.model

import java.time.OffsetDateTime

data class CheckResponse (
    val status: Int,
    val fileId: String? = null,
    val jobId: String? = null,
    val signer: String? = null,
    val timestamp: OffsetDateTime,
    val trace: List<String>?,
    val proof: Proof
)
