package com.sword.signature.api.sign

data class SignRequest(
    val fileName: String,
    val hash: String,
    val algorithm: String
)

data class SignResponse(
    val jobId: String
)