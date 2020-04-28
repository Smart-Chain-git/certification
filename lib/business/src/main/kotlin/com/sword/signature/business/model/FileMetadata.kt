package com.sword.signature.business.model

data class FileMetadata(
    val fileName: String,
    val fileSize: String? = null,
    val fileComment: String? = null,
    val batchComment: String? = null
)