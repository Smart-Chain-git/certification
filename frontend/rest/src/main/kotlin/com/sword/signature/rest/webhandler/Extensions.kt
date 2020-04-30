package com.sword.signature.rest.webhandler

import com.sword.signature.business.model.Account
import com.sword.signature.rest.authentication.CustomUserDetails
import com.sword.signature.rest.authentication.SignatureAuthenticationToken
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.web.reactive.function.server.ServerRequest

suspend fun ServerRequest.getAccount() : Account? {
    val signatureAuthenticationToken = principal().awaitFirstOrNull() as SignatureAuthenticationToken?
    return (signatureAuthenticationToken?.principal as CustomUserDetails?)?.account
}