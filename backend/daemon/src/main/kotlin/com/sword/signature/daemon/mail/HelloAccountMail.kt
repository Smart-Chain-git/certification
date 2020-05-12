package com.sword.signature.daemon.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context

class HelloAccountMail(recipient: Account) : TransactionalMail(
    recipient = recipient,
    templateName = "hello_account_mail",
    subject = subjectTag + "Hello"
) {
    override fun getContext() = Context().apply {
        setVariable("name", recipient.fullName)
    }
}
