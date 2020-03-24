package com.sword.signature.business.configuration

import com.sword.signature.business.job.MailJob
import com.sword.signature.business.model.mail.TransactionalMail
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler

@Configuration
@ComponentScan("com.sword.signature")
@EnableIntegration
@IntegrationComponentScan("com.sword.signature.business.service")
class IntegrationConfiguration(
        private val mailJob: MailJob
) {
    @Bean
    fun transactionalMailChannel() = QueueChannel()

    @Bean
    @ServiceActivator(inputChannel = "transactionalMailChannel", poller = [Poller(fixedRate = "\${spring.integration.poller.fixedRate}")])
    fun transactionalMailHandler(): MessageHandler {
        return MessageHandler { message: Message<*> ->
            mailJob.sendEmail(message.payload as TransactionalMail)
        }
    }
}
