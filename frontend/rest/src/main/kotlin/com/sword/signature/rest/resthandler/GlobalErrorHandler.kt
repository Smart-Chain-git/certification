package com.sword.signature.rest.resthandler

import com.fasterxml.jackson.databind.ObjectMapper
import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.exception.UserServiceException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Component
@Order(-2)
class GlobalErrorHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val bufferFactory = exchange.response.bufferFactory()
        val httpStatus: HttpStatus = when (ex) {
            is AuthenticationException, is org.springframework.security.core.AuthenticationException -> HttpStatus.FORBIDDEN
            is UserServiceException -> HttpStatus.BAD_REQUEST
            is ServiceException -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        val dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(HttpError(ex, httpStatus)))
        exchange.response.statusCode = httpStatus
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return exchange.response.writeWith(Mono.just(dataBuffer))
    }

    class HttpError {
        var message: String?
        var timestamp: OffsetDateTime
        var status: Int
        var error: String

        constructor(ex: Throwable, httpStatus: HttpStatus) {
            message = ex.message
            timestamp = OffsetDateTime.now()
            status = httpStatus.value()
            error = ex.toString()
        }
    }
}
