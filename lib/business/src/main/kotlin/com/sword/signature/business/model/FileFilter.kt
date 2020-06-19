package com.sword.signature.business.model

import java.time.LocalDate

data class FileFilter(
    val id: String? = null,
    val name: String? = null,
    val hash: String? = null,
    val jobId: String? = null,
    val accountId: String? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)
