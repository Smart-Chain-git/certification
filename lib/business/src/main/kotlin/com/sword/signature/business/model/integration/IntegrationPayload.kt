package com.sword.signature.business.model.integration

import com.sword.signature.business.model.Account
import java.io.Serializable


data class AnchorJobMessagePayload(
    val id: String
) : Serializable


data class TransactionalMailPayload(
    val type: TransactionalMailType,
    val recipient: Account
) : Serializable

enum class TransactionalMailType {
    HELLO_ACCOUNT
}

data class CallBackJobMessagePayload(
    val url: String,
    val numberOfTry: Int = 0
) : Serializable