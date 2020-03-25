package com.sword.signature.business.job

import com.sword.signature.business.model.mail.TransactionalMail
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
        val mailMessage = SimpleMailMessage()
        mailMessage.setFrom(sender)
        mailMessage.setTo(transactionalMail.recipient.email)
        mailMessage.setSubject(transactionalMail.subject)
        mailMessage.setText(templateEngine.process(transactionalMail.templateName, transactionalMail.getContext()))

        emailSender.send(mailMessage)
    }
}