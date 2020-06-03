package com.sword.signature.tezos.reader.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class TezosReaderConfiguration {

    @Bean
    fun webClient(webClientBuilder: WebClient.Builder, @Value("\${tezos.indexer.url}") indexerUrl: String): WebClient {
        return webClientBuilder
            .baseUrl(indexerUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}