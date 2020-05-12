package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.integration.TransactionalMailType


interface MailService {

    fun sendEmail(type: TransactionalMailType, recipient: Account)
}