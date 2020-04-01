package com.sword.signature.api.sign

const val ALGORITHM_MIME_TYPE = "message/x.sign.algorithm"

data class SignRequest(
    val fileName: String,
    val hash: String
)

data class SignResponse(
    val jobId: String,
    val files: List<String>
)