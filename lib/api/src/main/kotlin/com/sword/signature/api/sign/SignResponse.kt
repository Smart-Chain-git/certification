package com.sword.signature.api.sign

data class SignResponse(
    val jobId: String,
    val files: List<SignMetadata>
)
