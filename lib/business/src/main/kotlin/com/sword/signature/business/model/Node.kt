package com.sword.signature.business.model


data class FileHash(
    val hash: String,
    val fileName: String,
    val algorithm: String
)

data class SignJob(
    val id:String
)
//data class Node(
//    val id: String,
//    val hash: String,
//    val fileName: String,
//    val parendId: String?,
//    val position: String,
//    val jobId: String,
//    val type: String
//
//)