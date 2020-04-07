package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JwtTokenService
import com.sword.signature.business.service.TokenService
import com.sword.signature.model.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
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
        val token = tokenRepository.findByJwtToken(token).awaitFirstOrNull()?.toBusiness()
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

    override suspend fun getTokensByAccountId(accountId: String): Flow<Token> {
        LOGGER.debug("Retrieving all tokens with accountId '{}'.", accountId)
        val tokens = tokenRepository.findByAccountId(accountId).asFlow().map { it.toBusiness() }
        LOGGER.debug("Retrieved all tokens retrieved for accountId '{}'", accountId)
        return tokens
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenServiceImpl::class.java)
    }
}