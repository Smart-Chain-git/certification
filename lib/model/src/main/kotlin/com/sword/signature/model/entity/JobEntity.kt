package com.sword.signature.model.entity

import com.sword.signature.common.enums.JobStateType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime


@Document(collection = "jobs")
data class JobEntity(
    @Id
    val id: String? = null,

    /**
     * Date of the request
     */
    @CreatedDate
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
    /**
     * Hash of inject transaction
     */
    val transactionHash: String? = null,
    val blockHash: String? = null,
    /**
     * depth of the block at validation date
     */
    val blockDepth: Long? = null,

    /**
     * Algotithm used to build the Merkel tree
     */
    val algorithm: String,

    /**
     * user asking for the signature
     */
    @Indexed
    val userId: String,

    /**
     * Name of the input Flow
     */
    val flowName: String,

    var stateDate: OffsetDateTime,

    var state: JobStateType,

    /**
     * url to call when job is anchored
     */
    val callBackUrl: String?,

    /**
     * Address of the smart contract in the blockchain.
     */
    val contractAddress: String? = null
)

