package com.sword.signature.common.criteria

import java.time.LocalDate

data class JobCriteria(
    val id: String? = null,
    val accountId: String? = null,
    val flowName: String? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null,
    val channelName: String? = null
)