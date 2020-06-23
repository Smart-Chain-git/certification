package com.sword.signature.api.sign

data class SignRequest(
    val algorithm: String,
    val flowName: String,
    val callBackUrl: String?,
    val customFields: Map<String, String>? = null,
    val files: List<SingleSignRequest>
)