package com.sword.signature.daemon.configuration

import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.model.integration.TransactionalMailPayload
import com.sword.signature.business.model.integration.TransactionalMailType
import com.sword.signature.daemon.job.AnchorJob
import com.sword.signature.daemon.job.CallBackJob
import com.sword.signature.daemon.job.MailJob
import com.sword.signature.daemon.mail.HelloAccountMail
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.ReactiveMessageHandler


@Configuration
class DaemonIntegrationConfiguration(
    private val anchorJob: AnchorJob,
    private val mailJob: MailJob,
    private val callBackJob: CallBackJob
) {


// je garde pour mémoire si on doit un jour recharger les message en bourrin
//    @Bean
//    fun filesFlow(reactiveMongoTemplate: ReactiveMongoOperations) = integrationFlow(
//            jobService.findAllByUser(adminAccount, adminAccount).map { GenericMessage(it) }.asPublisher()
//        ) {
//            filter<Job> { it.state == JobStateType.INSERTED }
//            transform<Job> { AnchorJobMessagePayload(it.id) }
//            channel(jobToAnchorsMessageChannel)
//        }
//    }


    @Bean
    @ServiceActivator(
        inputChannel = "jobToAnchorsMessageChannel",
        poller = [Poller(fixedRate = "\${spring.integration.poller.fixedRate}")]
    )
    fun jobToAnchorsHandler(): ReactiveMessageHandler {
        return ReactiveMessageHandler { message: Message<*> ->
            mono {
                anchorJob.anchor(message.payload as AnchorJobMessagePayload)
                null
            }
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "transactionalMailChannel",
        poller = [Poller(fixedRate = "\${spring.integration.poller.fixedRate}")]
    )
    fun transactionalMailHandler(): MessageHandler {
        return MessageHandler { message: Message<*> ->
            val payload = message.payload as TransactionalMailPayload

            val email = when (payload.type) {
                TransactionalMailType.HELLO_ACCOUNT -> HelloAccountMail(payload.recipient)
            }

            mailJob.sendEmail(email)
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "callBackMessageChannel",
        poller = [Poller(fixedRate = "\${spring.integration.poller.fixedRate}")]
    )
    fun callBackMessageHandler() = ReactiveMessageHandler { message: Message<*> ->
        mono {
            callBackJob.callBack(message.payload as CallBackJobMessagePayload)
            null
        }
    }


}