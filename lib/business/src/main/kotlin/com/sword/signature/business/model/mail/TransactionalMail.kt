package com.sword.signature.business.model.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context
import java.io.Serializable

abstract class TransactionalMail(
    val templateName: String,
    val recipient: Account,
    val subject: String
) : Serializable {

    abstract fun getContext(): Context

    companion object {
        const val subjectTag = "[Tezos Signature] "
    }

}
