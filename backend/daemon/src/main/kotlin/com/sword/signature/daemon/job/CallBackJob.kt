package com.sword.signature.daemon.job

import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.daemon.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Component
class CallBackJob(
    private val webClientBuilder: WebClient.Builder
) {


    suspend fun callBack(callBackJobMessage: CallBackJobMessagePayload) {
        LOGGER.debug(
            "callback try n° {} for job {} at {}",
            callBackJobMessage.numberOfTry,
            callBackJobMessage.jobId,
            callBackJobMessage.url
        )
        val client = webClientBuilder.baseUrl(callBackJobMessage.url).build()

        val result = client.get().awaitExchange()
        val status = result.statusCode()
        if (status.is4xxClientError || status.is5xxServerError) {
            LOGGER.warn("fail to call {} for the {} time", callBackJobMessage.url, callBackJobMessage.numberOfTry)
        } else {
            LOGGER.debug("callback {} success", callBackJobMessage.url)
        }
    }

    companion object {
        private val LOGGER = logger()
    }
}