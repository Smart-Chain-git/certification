package com.sword.signature.business.service

import com.sword.signature.business.model.mail.TransactionalMail

interface MailService {

    fun sendEmail(transactionMail: TransactionalMail)
}