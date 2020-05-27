package com.sword.signature.business.model

import com.sword.signature.common.enums.JobStateType

data class JobPatch(
    val numberOfTry: Int? = null,
    val transactionHash: String? = null,
    val blockHash: String? = null,
    /**
     * depth of the block at validation date
     */
    val blockDepth: Long? = null,
    var state: JobStateType? = null,
    val contractAddress: String? = null
)