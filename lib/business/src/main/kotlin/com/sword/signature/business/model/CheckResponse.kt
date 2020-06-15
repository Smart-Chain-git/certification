package com.sword.signature.business.model

import java.time.OffsetDateTime

data class CheckResponse (
    val status: Int,
    val signer: String? = null,
    val timestamp: OffsetDateTime,
    val trace: List<String>,
    val proof: Proof
)