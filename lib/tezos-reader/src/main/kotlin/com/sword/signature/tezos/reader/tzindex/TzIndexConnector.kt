package com.sword.signature.tezos.reader.tzindex

import com.sword.signature.tezos.reader.TzException
import com.sword.signature.tezos.reader.tzindex.model.TzBlock
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Component
class TzIndexConnector(
    private val webClient: WebClient
) {
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

    suspend fun getContract(contractAddress: String): TzContract? {
        val response = webClient.get().uri("/explorer/contract/{contractAddress}", contractAddress).awaitExchange()
        val status = response.statusCode()

        if (status.is4xxClientError) {
            return null
        }

        if (status.is5xxServerError) {
            throw TzException.IndexerAccessException()
        }

        return response.bodyToMono(TzContract::class.java).awaitSingle()
    }
}