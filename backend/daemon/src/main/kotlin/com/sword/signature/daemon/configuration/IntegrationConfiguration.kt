package com.sword.signature.daemon.configuration

import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.daemon.job.AnchorJob
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message
import org.springframework.messaging.ReactiveMessageHandler


@Configuration
class DaemonIntegrationConfiguration(
    private val anchorJob: AnchorJob
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
    @ServiceActivator(inputChannel = "jobToAnchorsMessageChannel", poller = [Poller(fixedRate = "\${spring.integration.poller.fixedRate}")])
    fun jobToAnchorsHandler(): ReactiveMessageHandler {
        return ReactiveMessageHandler { message: Message<*> ->
            mono {
                anchorJob.anchor(message.payload as AnchorJobMessagePayload)
                null
            }
        }
    }


    companion object {
        val Logger: Logger = LoggerFactory.getLogger(DaemonIntegrationConfiguration::class.java)
    }

}