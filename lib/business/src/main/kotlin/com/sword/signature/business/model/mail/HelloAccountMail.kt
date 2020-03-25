package com.sword.signature.business.model.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context

data class HelloAccountMail(override val recipient: Account) : TransactionalMail() {
    override val templateName: String get() = "hello_account_mail"
    override val subject: String get() = subjectTag + "Hello"

    override fun getContext(): Context {
        val context = Context()
        context.setVariable("name", recipient.fullName)
        return context
    }
}
