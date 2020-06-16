package com.sword.signature.api.proof

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Proof(
    val version: String = "1.0.0",
    @JsonProperty("signature_date") val signatureDate: OffsetDateTime?,
    @JsonProperty("file_name") val filename: String?,
    @JsonProperty("hash_root") val rootHash: String,
    @JsonProperty("hash_document") val documentHash: String,
    @JsonProperty("hash_list") val hashes: List<HashNode>,
    val origin: String = "SWORD",
    val urls: List<UrlNode> = emptyList(),
    val algorithm: String,
    @JsonProperty("public_key") val signerAddress: String? = null,
    @JsonProperty("origin_public_key") val creatorAddress: String? = null,
    @JsonProperty("custom_fields") val customFields: Map<String, Any>?,
    @JsonProperty("contract_address") val contractAddress: String? = null,
    @JsonProperty("transaction_hash") val transactionHash: String? = null,
    @JsonProperty("block_hash") val blockHash: String? = null,
    @JsonProperty("block_depth") val blockDepth: Long? = null
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class HashNode(
        val hash: String?,
        val position: String
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class UrlNode(
        val url: String,
        val type: String,
        val comment: String? = null
    )
}
