package com.sword.signature.daemon.job

import com.sword.signature.business.model.mail.TransactionalMail
import com.sword.signature.daemon.logger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine

@Component
class MailJob(
    private val emailSender: JavaMailSender,
    @Qualifier("templateEngine") private val templateEngine: TemplateEngine,
    @Value("\${spring.mail.sender}") private val sender: String
) {
    fun sendEmail(transactionalMail: TransactionalMail) {
        LOGGER.trace("email send for {} subject {}", transactionalMail.recipient.email, transactionalMail.subject)
        val mimeMessage = emailSender.createMimeMessage()
        mimeMessage.subject = transactionalMail.subject
        val messageHelper = MimeMessageHelper(mimeMessage, true)
        messageHelper.setFrom(sender)
        messageHelper.setTo(transactionalMail.recipient.email)
        messageHelper.setText(templateEngine.process(transactionalMail.templateName, transactionalMail.getContext()))

        emailSender.send(mimeMessage)
    }

    companion object {
        val LOGGER: Logger = logger()
    }
}
