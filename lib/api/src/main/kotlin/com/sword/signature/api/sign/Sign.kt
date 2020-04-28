package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude

const val ALGORITHM_MIME_TYPE = "message/x.sign.algorithm"
const val FLOW_NAME_MIME_TYPE = "message/x.sign.flowName"

data class SignRequest(
    val metadata: SignMetadata,
    val hash: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SignMetadata(
    val fileName: String,
    val fileSize: String? = null,
    val fileComment: String? = null,
    val batchComment: String? = null
)

data class SignResponse(
    val jobId: String,
    val files: List<SignMetadata>
)