package com.sword.signature.common.criteria

import com.sword.signature.common.enums.TreeElementType

data class TreeElementCriteria(
    val jobId: String? = null,
    val type: TreeElementType? = null
)