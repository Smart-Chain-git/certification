package com.sword.signature.business.service.impl

import com.sword.signature.business.model.mail.TransactionalMail
import com.sword.signature.business.service.MailService
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
        private val transactionalMailChannel: MessageChannel
) : MailService {

    override fun sendEmail(transactionalMail: TransactionalMail) {
        transactionalMailChannel.send(MessageBuilder.withPayload(transactionalMail).build())
    }
}