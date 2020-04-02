package com.sword.signature.api.sign

const val ALGORITHM_MIME_TYPE = "message/x.sign.algorithm"
const val FLOW_NAME_MIME_TYPE = "message/x.sign.flowName"

data class SignRequest(
    val fileName: String,
    val hash: String
)

data class SignResponse(
    val jobId: String,
    val files: List<String>
)