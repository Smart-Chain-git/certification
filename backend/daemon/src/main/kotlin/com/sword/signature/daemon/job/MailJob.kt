package com.sword.signature.daemon.job

import com.sword.signature.daemon.mail.TransactionalMail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine

@Component
class MailJob(
        private val emailSender: JavaMailSender,
        @Qualifier("templateEngine") private val templateEngine: TemplateEngine,
        @Value("\${spring.mail.sender}") private val sender: String
) {
    fun sendEmail(transactionalMail: TransactionalMail) {
        LOGGER.trace("email send for {} subject {}",transactionalMail.recipient.email,transactionalMail.subject)
        val mailMessage = SimpleMailMessage()
        mailMessage.setFrom(sender)
        mailMessage.setTo(transactionalMail.recipient.email)
        mailMessage.setSubject(transactionalMail.subject)

        mailMessage.setText(templateEngine.process(transactionalMail.templateName, transactionalMail.getContext()))

        emailSender.send(mailMessage)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(MailJob::class.java)
    }
}