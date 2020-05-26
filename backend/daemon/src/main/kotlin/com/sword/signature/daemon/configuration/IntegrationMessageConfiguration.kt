package com.sword.signature.daemon.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.mongodb.store.MongoDbChannelMessageStore
import org.springframework.integration.store.MessageGroupQueue

@Configuration
class IntegrationMessageConfiguration {


    @Bean
    fun callBackMessageChannel(mongoDbChannelMessageStore: MongoDbChannelMessageStore) =
        QueueChannel(MessageGroupQueue(mongoDbChannelMessageStore, "callBack"))

    @Bean
    fun callBackErrorMessageChannel(
        mongoDbChannelMessageStore: MongoDbChannelMessageStore
    ) = QueueChannel(
        MessageGroupQueue(mongoDbChannelMessageStore, "callBackError")
    )

    @Bean
    fun validationMessageChannel(
        mongoDbChannelMessageStore: MongoDbChannelMessageStore
    ) = QueueChannel(MessageGroupQueue(mongoDbChannelMessageStore, "validation"))

    @Bean
    fun validationRetryMessageChannel(
        mongoDbChannelMessageStore: MongoDbChannelMessageStore
    ) = QueueChannel(MessageGroupQueue(mongoDbChannelMessageStore, "validationRetry"))

}
