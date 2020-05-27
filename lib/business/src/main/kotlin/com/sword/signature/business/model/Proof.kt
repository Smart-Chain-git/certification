package com.sword.signature.business.model

import com.sword.signature.common.enums.TreeElementPosition
import java.time.OffsetDateTime

data class Proof(
    val signatureDate: OffsetDateTime? = null,
    val filename: String?,
    val rootHash: String,
    val documentHash: String,
    val hashes: List<Pair<String?, TreeElementPosition>> = emptyList(),
    val algorithm: String,
    val customFields: Map<String, Any>? = null,
    val contractAddress: String? = null,
    val transactionHash: String? = null,
    val blockHash: String? = null
)