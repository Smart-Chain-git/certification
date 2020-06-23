package com.sword.signature.rsocket.rsocketcontroler

import com.sword.signature.api.sign.SignResponse
import com.sword.signature.api.sign.SingleSignRequest
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.AlgorithmService
import com.sword.signature.business.service.SignService
import com.sword.signature.webcore.authentication.CustomUserDetails
import com.sword.signature.webcore.mapper.toBusiness
import com.sword.signature.webcore.mapper.toWebSignResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class SignController(
    val accountService: AccountService,
    val signService: SignService,
    val algorithmService: AlgorithmService
) {

    @MessageMapping("newJobs")
    fun batchSign(
        @AuthenticationPrincipal user: CustomUserDetails,
        @Header(name = "algorithm") algorithmParameter: String?,
        @Header(name = "flowName") flowName: String?,
        @Header(name = "callBackUrl") callBackUrl: String?,
        requests: Flow<SingleSignRequest>
    ): Flow<SignResponse> {

        if (algorithmParameter == null) {
            throw IllegalArgumentException("missing algorithm parameter")
        }

        if (flowName == null) {
            throw IllegalArgumentException("missing flowName parameter")
        }

        val algorithm = runBlocking { algorithmService.getAlgorithmByName(algorithmParameter) }

        return signService.batchSign(
            requester = user.account,
            channelName = user.channelName,
            algorithm = algorithm,
            flowName = flowName,
            callBackUrl = callBackUrl,
            fileHashes = requests.map { it.toBusiness() })
            .map { it.toWebSignResponse() }
    }
}
