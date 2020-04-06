package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JwtTokenService
import com.sword.signature.business.service.TokenService
import com.sword.signature.model.repository.TokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TokenServiceImpl(
        private val tokenRepository: TokenRepository,
        private val jwtTokenService: JwtTokenService
) : TokenService {

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getToken(token: String): Token? {
        LOGGER.debug("Retrieving token with jwtToken '{}'.", token)
        val token = tokenRepository.findByJwtToken(token)?.toBusiness()
        LOGGER.debug("Token with jwtToken '{}' retrieved.", token)
        return token
    }

    override suspend fun checkAndGetToken(jwtToken: String): Token {
        // Check JWT token validity.
        jwtTokenService.parseToken(jwtToken)
        // Check that token has not been revoked.
        val token: Token = getToken(jwtToken) ?: throw AuthenticationException.RevokedTokenException(jwtToken)
        // Check expiration date.
        token.expirationDate?.let {
            if (it < LocalDate.now()) {
                throw AuthenticationException.ExpiredTokenException(token)
            }
        }
        return token
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenServiceImpl::class.java)
    }
}