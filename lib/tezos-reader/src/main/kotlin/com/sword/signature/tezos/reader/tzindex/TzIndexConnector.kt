package com.sword.signature.tezos.reader.tzindex

import com.sword.signature.tezos.reader.TzException
import com.sword.signature.tezos.reader.tzindex.model.TzBlock
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Component
class TzIndexConnector(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${tezos.indexer.url}") private val indexerUrl: String
) {
    private val webClient: WebClient = webClientBuilder
        .baseUrl(indexerUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun getTransaction(transactionHash: String): TzOp? {
        val response = webClient.get().uri("/explorer/op/{opHash}", transactionHash).awaitExchange()
        val status = response.statusCode()

        if (status.is4xxClientError) {
            return null
        }

        if (status.is5xxServerError) {
            throw TzException.IndexerAccessException()
        }

        return response.bodyToFlux(TzOp::class.java).awaitFirst()
    }

    suspend fun getHead(): TzBlock {
        val response = webClient.get().uri("/explorer/block/head").awaitExchange()
        val status = response.statusCode()

        if (status.is5xxServerError) {
            throw TzException.IndexerAccessException()
        }

        return response.bodyToMono(TzBlock::class.java).awaitSingle()
    }
}