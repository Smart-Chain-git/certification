package com.sword.signature.business.model

import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.NotificationStatusType
import java.time.OffsetDateTime

data class JobPatch(
    val numberOfTry: Int? = null,
    val transactionHash: String? = null,
    val blockHash: String? = null,
    /**
     * Block depth at validation date.
     */
    val blockDepth: Long? = null,
    var state: JobStateType? = null,
    val callBackStatus: NotificationStatusType? = null,
    val contractAddress: String? = null,
    val signerAddress: String? = null,
    val timestamp: OffsetDateTime? = null
)
