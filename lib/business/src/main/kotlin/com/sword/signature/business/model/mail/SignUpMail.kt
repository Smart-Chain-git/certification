package com.sword.signature.business.model.mail

import com.sword.signature.business.model.Account
import org.thymeleaf.context.Context

class SignUpMail(
    recipient: Account,
    private val link: String
) : TransactionalMail(
    recipient = recipient,
    templateName = "signup_mail",
    subject = subjectTag + "Welcome"
) {
    override fun getContext() = Context().apply {
        setVariable("name", recipient.fullName)
        setVariable("link", link)
    }
}
