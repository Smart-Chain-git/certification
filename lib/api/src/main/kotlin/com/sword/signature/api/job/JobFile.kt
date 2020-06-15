package com.sword.signature.api.job

import com.fasterxml.jackson.annotation.JsonInclude
import com.sword.signature.api.proof.Proof
import com.sword.signature.api.sign.SignMetadata

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JobFile(
    val id: String,
    val hash: String,
    val jobId: String,
    val metadata: SignMetadata,
    val proof: Proof?
)
