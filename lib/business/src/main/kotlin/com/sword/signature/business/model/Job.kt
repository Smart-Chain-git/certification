package com.sword.signature.business.model

import com.sword.signature.common.enums.JobStateType
import java.time.OffsetDateTime

data class Job(
    val id: String,

    /**
     * Date of the request
     */
    val createdDate: OffsetDateTime = OffsetDateTime.now(),

    /**
     * data of the last injection in the block chain
     */
    val injectedDate: OffsetDateTime? = null,
    /**
     * date of the validation in the block chain
     */
    val validatedDate: OffsetDateTime? = null,

    val numberOfTry: Int = 0,

    val transactionHash: String? = null,

    val blockId: String? = null,
    /**
     * depth of the block at validation date
     */
    val blockDepth: Int? = null,

    /**
     * Algotithm used to build the Merkel tree
     */
    val algorithm: String,

    /**
     * user asking for the signature
     */
    val userId: String,

    val flowName: String,

    var stateDate: OffsetDateTime,

    var state: JobStateType,

    /**
     * Hash of root element
     */
    val rootHash: String? = null,

    val files: List<TreeElement.LeafTreeElement>? = null

)