package com.sword.signature.api.check

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.sword.signature.api.proof.Proof
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class CheckRequest(
    val documentHash: String,
    val proof: String?
) {
}
