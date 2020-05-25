package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Proof(

    val version: String = "1.0.0",
    @JsonProperty("signature_date") val signatureDate: OffsetDateTime? = null,
    @JsonProperty("file_name") val filename: String? = null,
    @JsonProperty("hash_root") val rootHash: String,
    @JsonProperty("hash_document") val documentHash: String,
    @JsonProperty("hash_list") val hashes: List<HashNode>,
    val origin: String = "SWORD",
    val urls: List<UrlNode> = emptyList(),
    val algorithm: String,
    @JsonProperty("public_key")
    val publicKey: String,
    @JsonProperty("origin_public_key")
    val originPublicKey: String,
    val comment: String?=null

)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class HashNode(
    val hash: String?,
    val position: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UrlNode(
    val url: String,
    val Type: String,
    val comment: String? = null
)
