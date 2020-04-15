package com.sword.signature.business.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.JwtTokenDetails
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.TokenService
import com.sword.signature.model.entity.TokenEntity
import com.sword.signature.model.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime

@Service
class TokenServiceImpl(
        @Value("\${jwt.secret}") private val jwtSecret: String,
        @Value("\${jwt.issuer}") private val jwtIssuer: String,
        private val tokenRepository: TokenRepository
) : TokenService {

    private val algorithm = Algorithm.HMAC256(jwtSecret)

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun createToken(tokenDetails: TokenCreate): Token {
        LOGGER.debug("Creating new token.")
        val jwtToken = createToken(JwtTokenDetails(
                id = tokenDetails.accountId,
                creationTime = OffsetDateTime.now().toString()
        ))
        val toCreate = TokenEntity(
                name = tokenDetails.name,
                jwtToken = jwtToken,
                expirationDate = tokenDetails.expirationDate,
                accountId = tokenDetails.accountId
        )
        val createdToken = tokenRepository.save(toCreate).awaitSingle().toBusiness()
        LOGGER.debug("New token created with id '{}'", createdToken.id)
        return createdToken
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getToken(jwtToken: String): Token? {
        LOGGER.debug("Retrieving token with jwtToken '{}'.", jwtToken)
        val token = tokenRepository.findByJwtToken(jwtToken).awaitFirstOrNull()?.toBusiness()
        LOGGER.debug("Token with jwtToken '{}' retrieved.", jwtToken)
        return token
    }

    override suspend fun checkAndGetToken(jwtToken: String): Token {
        // Check JWT token validity.
        parseToken(jwtToken)
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

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun deleteToken(tokenId: String) {
        LOGGER.debug("Deleting token with id '{}'.", tokenId)
        val token: TokenEntity = tokenRepository.findById(tokenId).awaitFirstOrNull()
                ?: throw EntityNotFoundException("token", tokenId)
        tokenRepository.delete(token).awaitFirstOrNull()
        LOGGER.debug("Token with id '{}' deleted.", tokenId)
    }

    /**
     * Create a JWT token from the details and return its string representation.
     */
    internal fun createToken(jwtTokenDetails: JwtTokenDetails): String {
        return JWT.create()
                .withIssuer(jwtIssuer)
                .withClaim(CLAIM_ID, jwtTokenDetails.id)
                .withClaim(CLAIM_CREATION_TIME, jwtTokenDetails.creationTime)
                .sign(algorithm)
    }

    /**
     * Parse the string representation of a JWT token, verify it, and return the details used to create it.
     */
    @Throws(JWTVerificationException::class)
    internal fun parseToken(token: String): JwtTokenDetails {
        val decodedJWT = JWT.require(algorithm)
                .withIssuer(jwtIssuer)
                .build()
                .verify(token)
        return JwtTokenDetails(
                id = decodedJWT.claims[CLAIM_ID]?.asString() ?: "",
                creationTime = decodedJWT.claims[CLAIM_CREATION_TIME]?.asString() ?: ""
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenServiceImpl::class.java)

        private const val CLAIM_ID = "id"
        private const val CLAIM_CREATION_TIME = "creationTime"
    }
}