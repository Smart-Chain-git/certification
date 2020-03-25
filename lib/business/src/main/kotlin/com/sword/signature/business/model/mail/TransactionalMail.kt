package com.sword.signature.business.model.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context

abstract class TransactionalMail {
    abstract val templateName: String
    abstract val subject: String
    abstract val recipient: Account

    val subjectTag get() = "[Tezos Signature] "

    abstract fun getContext(): Context
}
