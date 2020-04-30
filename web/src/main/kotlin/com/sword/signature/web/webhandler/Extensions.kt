package com.sword.signature.web.webhandler

import com.sword.signature.business.model.Account
import com.sword.signature.web.authentication.CustomUserDetails
import com.sword.signature.web.authentication.SignatureAuthenticationToken
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.web.reactive.function.server.ServerRequest

suspend fun ServerRequest.getAccount() : Account? {
    val signatureAuthenticationToken = principal().awaitFirstOrNull() as SignatureAuthenticationToken?
    return (signatureAuthenticationToken?.principal as CustomUserDetails?)?.account
}