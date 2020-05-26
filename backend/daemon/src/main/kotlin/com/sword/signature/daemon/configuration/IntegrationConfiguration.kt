package com.sword.signature.daemon.configuration

import com.sword.signature.business.model.integration.*
import com.sword.signature.daemon.job.AnchorJob
import com.sword.signature.daemon.job.CallBackJob
import com.sword.signature.daemon.job.MailJob
import com.sword.signature.daemon.job.ValidationJob
import com.sword.signature.daemon.mail.HelloAccountMail
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.handler.DelayHandler
import org.springframework.integration.mongodb.store.ConfigurableMongoDbMessageStore
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.ReactiveMessageHandler
import java.time.Duration


@Configuration
class DaemonIntegrationConfiguration(
    private val anchorJob: AnchorJob,
    private val mailJob: MailJob,
    private val callBackJob: CallBackJob,
    private val validationJob: ValidationJob
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
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
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
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
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
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun callBackMessageHandler() = ReactiveMessageHandler { message: Message<*> ->
        mono {
            callBackJob.callBack(message.payload as CallBackJobMessagePayload)
            null
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "callBackErrorMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun callBackErrorMessageChannelDelayer(
        @Value("\${daemon.callback.delay}") delay: Duration,
        configurableMongoDbMessageStore: ConfigurableMongoDbMessageStore
    ) =
        DelayHandler("callBackErrorMessageChannelDelayer").apply {
            setMessageStore(configurableMongoDbMessageStore)
            setDefaultDelay(delay.toMillis())
            setOutputChannelName("callBackMessageChannel")
        }

    @Bean
    @ServiceActivator(
        inputChannel = "validationMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun validationMessageChannelHandler() = ReactiveMessageHandler { message: Message<*> ->
        mono {
            validationJob.validate(message.payload as ValidationJobMessagePayload)
            null
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "validationRetryMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun validationRetryMessageChannelDelayer(
        @Value("\${daemon.validation.delay}") delay: Duration,
        configurableMongoDbMessageStore: ConfigurableMongoDbMessageStore
    ) = DelayHandler("validationRetryMessageChannelDelayer").apply {
        setMessageStore(configurableMongoDbMessageStore)
        setDefaultDelay(delay.toMillis())
        setOutputChannelName("validationMessageChannel")
    }
}