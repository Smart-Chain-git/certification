package com.sword.signature.daemon.configuration

import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
import com.sword.signature.business.model.mail.TransactionalMail
import com.sword.signature.daemon.job.AnchorJob
import com.sword.signature.daemon.job.CallBackJob
import com.sword.signature.daemon.job.MailJob
import com.sword.signature.daemon.job.ValidationJob
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
    @Bean
    @ServiceActivator(
        inputChannel = "anchoringMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun jobToAnchorHandler(): ReactiveMessageHandler {
        return ReactiveMessageHandler { message: Message<*> ->
            mono {
                anchorJob.anchor(message.payload as AnchorJobMessagePayload)
                null
            }
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "anchoringRetryMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun anchoringRetryMessageChannelDelayer(
        @Value("\${daemon.anchoring.delay}") delay: Duration,
        configurableMongoDbMessageStore: ConfigurableMongoDbMessageStore
    ) =
        DelayHandler("anchoringRetryMessageChannelDelayer").apply {
            setMessageStore(configurableMongoDbMessageStore)
            setDefaultDelay(delay.toMillis())
            setOutputChannelName("anchoringMessageChannel")
        }

    @Bean
    @ServiceActivator(
        inputChannel = "transactionalMailChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun transactionalMailHandler(): MessageHandler {
        return MessageHandler { message: Message<*> ->
            val transactionalMail = message.payload as TransactionalMail

            mailJob.sendEmail(transactionalMail)
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "callbackMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun callbackMessageHandler() = ReactiveMessageHandler { message: Message<*> ->
        mono {
            callBackJob.callBack(message.payload as CallBackJobMessagePayload)
            null
        }
    }

    @Bean
    @ServiceActivator(
        inputChannel = "callbackRetryMessageChannel",
        poller = [Poller(fixedRate = "\${daemon.poller.fixedRate}")]
    )
    fun callbackRetryMessageChannelDelayer(
        @Value("\${daemon.callback.delay}") delay: Duration,
        configurableMongoDbMessageStore: ConfigurableMongoDbMessageStore
    ) =
        DelayHandler("callbackRetryMessageChannelDelayer").apply {
            setMessageStore(configurableMongoDbMessageStore)
            setDefaultDelay(delay.toMillis())
            setOutputChannelName("callbackMessageChannel")
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
