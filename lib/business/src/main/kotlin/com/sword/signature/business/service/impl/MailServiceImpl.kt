package com.sword.signature.business.service.impl

import com.sword.signature.business.model.mail.TransactionalMail
import com.sword.signature.business.service.MailService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine

@Service
class MailServiceImpl(
        private val emailSender: JavaMailSender,
        @Qualifier("templateEngine") private val templateEngine: TemplateEngine,
        @Value("\${spring.mail.sender}") private val sender: String,
        private val transactionalMailChannel: MessageChannel
) : MailService {


    override fun sendEmail(transactionalMail: TransactionalMail) {
        val mailMessage = SimpleMailMessage()
        mailMessage.setFrom(sender)
        mailMessage.setTo(transactionalMail.recipient.email)
        mailMessage.setSubject(transactionalMail.subject)
        mailMessage.setText(templateEngine.process(transactionalMail.templateName, transactionalMail.getContext()))

        emailSender.send(mailMessage)
    }

    override fun sendEmailAsync(transactionalMail: TransactionalMail) {
        transactionalMailChannel.send(MessageBuilder.withPayload(transactionalMail).build())
    }
}