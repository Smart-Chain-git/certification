package com.sword.signature.api.sign

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SignMetadata(
    val fileName: String,
    val fileSize: String? = null,
    val customFields: Map<String, String>? = null
)
