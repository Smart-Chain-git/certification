package com.sword.signature.business.model.integration

import com.sword.signature.business.model.Account
import java.io.Serializable
import java.time.OffsetDateTime


data class AnchorJobMessagePayload(
    val requesterId: String,
    val jobId: String
) : Serializable


data class TransactionalMailPayload(
    val type: TransactionalMailType,
    val recipient: Account
) : Serializable

enum class TransactionalMailType {
    HELLO_ACCOUNT
}

data class CallBackJobMessagePayload(
    val jobId: String,
    val url: String,
    val numberOfTry: Int = 1
) : Serializable

data class ValidationJobMessagePayload(
    val requesterId: String,
    val jobId: String,
    val transactionHash: String,
    val injectionTime: OffsetDateTime
) : Serializable