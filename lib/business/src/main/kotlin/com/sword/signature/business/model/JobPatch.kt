package com.sword.signature.business.model

import com.sword.signature.common.enums.JobStateType
import java.time.OffsetDateTime

data class JobPatch(
    val numbreOfTry: Int? = null,
    val blockId: String? = null,
    /**
     * depth of the block at validation date
     */
    val blockDepth: Int? = null,

    var state: JobStateType?=null

)