package com.sword.signature.business.configuration


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.mongodb.store.MongoDbChannelMessageStore
import org.springframework.integration.store.MessageGroupQueue

@Configuration
@ComponentScan("com.sword.signature")
@EnableIntegration
@IntegrationComponentScan("com.sword.signature.business.service")
class IntegrationConfiguration{


    @Bean
    fun mongoDbChannelMessageStore(mongoDatabaseFactory: MongoDatabaseFactory) =
        MongoDbChannelMessageStore(mongoDatabaseFactory)


    @Bean
    fun jobToAnchorsMessageChannel(mongoDbChannelMessageStore: MongoDbChannelMessageStore) = QueueChannel(
        MessageGroupQueue(mongoDbChannelMessageStore, "jobToAnchor")
    )


    @Bean
    fun transactionalMailChannel(mongoDbChannelMessageStore: MongoDbChannelMessageStore) = QueueChannel(MessageGroupQueue(mongoDbChannelMessageStore, "transactionalMail"))


}
