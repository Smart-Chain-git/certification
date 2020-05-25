package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude

const val ALGORITHM_MIME_TYPE = "message/x.sign.algorithm"
const val FLOW_NAME_MIME_TYPE = "message/x.sign.flowName"
const val CALLBACK_URL_MIME_TYPE = "message/x.sign.callBack"

data class SignRequest(
    val metadata: SignMetadata,
    val hash: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SignMetadata(
    val fileName: String,
    val fileSize: String? = null,
    val customFields: Map<String, String>? = null
)

data class SignResponse(
    val jobId: String,
    val files: List<SignMetadata>
)