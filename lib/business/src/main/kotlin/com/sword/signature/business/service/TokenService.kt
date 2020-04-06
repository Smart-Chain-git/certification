package com.sword.signature.business.service

import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.model.repository.TokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TokenService(
        private val tokenRepository: TokenRepository,
        private val jwtTokenService: JwtTokenService
) {

    @Transactional(rollbackFor = [ServiceException::class])
    suspend fun getToken(token: String): Token? {
        LOGGER.debug("Retrieving token with jwtToken ({}).", token)
        val token = tokenRepository.findByJwtToken(token)?.toBusiness()
        LOGGER.debug("Token with jwtToken ({}) retrieved.", token)
        return token
    }

    suspend fun checkAndGetToken(token: String): Token {
        val jwtToken = jwtTokenService.parseToken(token)

        val token = getToken(token)
        if (token != null) {
            if (token.expirationDate != null && token.expirationDate > LocalDate.now()) {
                throw UserServiceException("Token expired.")
            }

            return token
        }
        throw UserServiceException("Revoked token.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenService::class.java)
    }
}