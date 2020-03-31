package com.sword.signature.business.model


data class FileHash(
    val hash: String,
    val fileName: String,
    val algorithm: String
)

