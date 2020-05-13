package com.sword.signature.business.model

data class FileMetadata(
    val fileName: String,
    val fileSize: String? = null,
    val customFields: Map<String, String>? = null
)