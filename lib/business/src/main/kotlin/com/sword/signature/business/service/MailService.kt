package com.sword.signature.business.service

import com.sword.signature.business.model.mail.TransactionalMail

interface MailService {

    /**
     * Send an email.
     * @param transactionalMail Mail to send.
     */
    fun sendEmail(transactionalMail: TransactionalMail)
}
