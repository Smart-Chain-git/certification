package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.integration.TransactionalMailPayload
import com.sword.signature.business.model.integration.TransactionalMailType
import com.sword.signature.business.service.MailService
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
        private val transactionalMailChannel: MessageChannel
) : MailService {

    override fun sendEmail(type: TransactionalMailType,recipient: Account) {

        val transactionalMailPayload =TransactionalMailPayload(
            type= type,
            recipient = recipient
        )
        transactionalMailChannel.send(MessageBuilder.withPayload(transactionalMailPayload).build())
    }
}