package com.sword.signature.business.model.integration

import java.io.Serializable
import java.time.OffsetDateTime

data class AnchorJobMessagePayload(
    val requesterId: String,
    val jobId: String
) : Serializable

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