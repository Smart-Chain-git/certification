package com.sword.signature.daemon.job

import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.daemon.logger
import com.sword.signature.daemon.sendPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.lang.Exception

@Component
class CallBackJob(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${daemon.callback.maxTry}") private val maxTry: Int,
    private val callbackRetryMessageChannel: MessageChannel
) {


    suspend fun callBack(callBackJobMessage: CallBackJobMessagePayload) {
        LOGGER.debug(
            "callback try n° {} for job ({}) at '{}'",
            callBackJobMessage.numberOfTry,
            callBackJobMessage.jobId,
            callBackJobMessage.url
        )

        var isError = false
        try {
            val client = webClientBuilder.baseUrl(callBackJobMessage.url).build()
            val result = client.get().awaitExchange()
            val status = result.statusCode()
            if (status.is4xxClientError || status.is5xxServerError) {
                isError = true
            }
        } catch (e: Exception) {
            LOGGER.error("error durring callbackJob : {}", e.message)
            isError = true
        }
        if (isError) {
            LOGGER.warn("fail to call '{}' for the {} time", callBackJobMessage.url, callBackJobMessage.numberOfTry)
            if (callBackJobMessage.numberOfTry < maxTry) {
                LOGGER.warn("will try again later")
                callbackRetryMessageChannel.sendPayload(callBackJobMessage.copy(numberOfTry = callBackJobMessage.numberOfTry + 1))
            } else {
                LOGGER.warn("it was the last try")
            }
        } else {
            LOGGER.debug("callback {} success", callBackJobMessage.url)
        }
    }

    companion object {
        private val LOGGER = logger()
    }
}

