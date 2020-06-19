package com.sword.signature.common.criteria

data class FileCriteria(
    val id: String? = null,
    val name: String? = null,
    val hash: String? = null,
    val jobId: String? = null,
    val jobIds: List<String>? = null
)
