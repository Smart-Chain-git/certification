package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Proof(
    @JsonProperty("signature_date")
    val signatureDate: OffsetDateTime? = null,
    @JsonProperty("file_name")
    val fileName: String? = null,
    val branch: Branch,
    val origin: String = "SWORD",
    val urls: List<String> = emptyList(),
    val algorithm: String,
    @JsonProperty("public_key")
    val publicKey: String,
    @JsonProperty("origin_public_key")
    val originPublicKey: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Branch(
    val hash: String,
    val position: String? = null,
    val par: Branch? = null
)
