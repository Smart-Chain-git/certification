package com.sword.signature.daemon.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context

abstract class TransactionalMail(
    val templateName: String,
    val recipient: Account,
    val subject: String
) {

    abstract fun getContext(): Context

    companion object {
        const val subjectTag = "[Tezos Signature] "
    }

}
