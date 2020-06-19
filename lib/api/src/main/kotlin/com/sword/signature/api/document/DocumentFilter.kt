package com.sword.signature.api.document

import java.time.LocalDate

data class DocumentFilter(
    val id: String? = null,
    val name: String? = null,
    val hash: String? = null,
    val jobId: String? = null,
    val accountId: String? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)
