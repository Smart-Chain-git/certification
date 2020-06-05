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

    val blockHash: String? = null,
    /**
     * Block depth at validation date.
     */
    val blockDepth: Long? = null,

    /**
     * Algotithm used to build the Merkel tree
     */
    val algorithm: String,

    /**
     * user asking for the signature
     */
    val userId: String,

    val flowName: String,

    val stateDate: OffsetDateTime,

    val state: JobStateType,

    /**
     * url to call when job is anchored
     */
    val callBackUrl: String? = null,

    /**
     * Hash of root element
     */
    val rootHash: String? = null,

    val files: List<TreeElement.LeafTreeElement>? = null,

    /**
     * Address of the smart contract in the blockchain.
     */
    val contractAddress: String? = null,

    /**
     * Address of the transaction signer.
     */
    val signerAddress: String? = null
)