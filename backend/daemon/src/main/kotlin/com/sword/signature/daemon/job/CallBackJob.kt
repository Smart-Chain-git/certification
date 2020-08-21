package com.sword.signature.daemon.job

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.CallBackJobMessagePayload
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.NotificationStatusType
import com.sword.signature.daemon.logger
import com.sword.signature.daemon.sendPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Component
class CallBackJob(
    private val jobService: JobService,
    private val webClientBuilder: WebClient.Builder,
    @Value("\${daemon.callback.maxTry}") private val maxTry: Int,
    private val callbackRetryMessageChannel: MessageChannel
) {
    private val jobIdPlaceholder = "\$jobId"

    private val dummyAdminAccount = Account(
        id = "", login = "", password = "", fullName = "", email = "", company = null,
        country = null, publicKey = null, hash = null, isAdmin = true, disabled = false, firstLogin = false
    )

    /**
     * Callback the signer of the job.
     * @param callBackJobMessage Job payload containing the callback infos.
     */
    suspend fun callBack(callBackJobMessage: CallBackJobMessagePayload) {
        LOGGER.debug(
            "Callback try n° {} for job {} at '{}'.",
            callBackJobMessage.numberOfTry,
            callBackJobMessage.jobId,
            callBackJobMessage.url
        )

        val updatedCallBackUrl = callBackJobMessage.url.replace(jobIdPlaceholder, callBackJobMessage.jobId)

        var isError = false
        try {
            val client = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .build()
            val result = client.get().uri(updatedCallBackUrl).awaitExchange()
            val status = result.statusCode()
            if (status.is4xxClientError || status.is5xxServerError) {
                LOGGER.error("GET {} returns a {} status.", updatedCallBackUrl, status.value())
                isError = true
            }
        } catch (e: Exception) {
            LOGGER.error("Error during callbackJob : {}.", e.message)
            isError = true
        }
        val newCallBackStatus: NotificationStatusType
        if (isError) {
            LOGGER.warn("Failed to call '{}' for the {} time.", updatedCallBackUrl, callBackJobMessage.numberOfTry)
            if (callBackJobMessage.numberOfTry < maxTry) {
                LOGGER.warn("Will try again later.")
                newCallBackStatus = NotificationStatusType.PENDING
                callbackRetryMessageChannel.sendPayload(callBackJobMessage.copy(numberOfTry = callBackJobMessage.numberOfTry + 1))
            } else {
                newCallBackStatus = NotificationStatusType.FAILURE
                LOGGER.warn("It was the last try.")
            }
        } else {
            newCallBackStatus = NotificationStatusType.COMPLETED
            LOGGER.debug("Callback {} success", updatedCallBackUrl)
        }
        if (newCallBackStatus != NotificationStatusType.PENDING) {
            // so updating in database
            jobService.patch(
                requester = dummyAdminAccount,
                jobId = callBackJobMessage.jobId,
                patch = JobPatch(
                    callBackStatus = newCallBackStatus
                )
            )
        }

    }

    companion object {
        private val LOGGER = logger()
    }
}
