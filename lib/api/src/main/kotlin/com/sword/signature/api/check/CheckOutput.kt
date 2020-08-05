package com.sword.signature.api.check

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.sword.signature.api.proof.Proof
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class CheckOutput(
    @JsonProperty("check") val output: String
) {
    class Ok(
        @JsonProperty("check_status") val status: Int,
        @JsonProperty("id") val fileId: String?,
        val jobId: String?,
        val signer: String?,
        val timestamp: OffsetDateTime,
        @JsonProperty("check_process") val process: List<String>,
        val proof: Proof
    ) : CheckOutput(output = "OK")

    class Ko(
        val error: String,
        @JsonProperty("hash_document") val documentHash: String? = null,
        @JsonProperty("hash_document_proof") val proofDocumentHash: String? = null,
        @JsonProperty("hash_root") val rootHash: String? = null,
        val signer: String? = null,
        @JsonProperty("public_key") val publicKey: String? = null,
        val date: OffsetDateTime? = null,
        @JsonProperty("current_depth") val currentDepth: Long? = null,
        @JsonProperty("expected_depth") val expectedDepth: Long? = null,
        @JsonProperty("origin_public_key") val originPublicKey: String? = null,
        @JsonProperty("proof_origin_public_key") val proofOriginPublicKey: String? = null
    ) : CheckOutput(output = "KO")
}
