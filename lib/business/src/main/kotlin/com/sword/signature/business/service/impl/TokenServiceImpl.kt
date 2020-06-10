package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.TokenPatch
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.TokenService
import com.sword.signature.model.entity.QTokenEntity
import com.sword.signature.model.entity.TokenEntity
import com.sword.signature.model.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository
) : TokenService {

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun createToken(tokenDetails: TokenCreate): Token {
        LOGGER.debug("Creating new token.")
        val toCreate = TokenEntity(
            name = tokenDetails.name,
            jwtToken = tokenDetails.jwtToken,
            expirationDate = tokenDetails.expirationDate,
            accountId = tokenDetails.accountId
        )
        val createdToken = tokenRepository.save(toCreate).awaitSingle().toBusiness()
        LOGGER.debug("New token created with id '{}'", createdToken.id)
        return createdToken
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun findAll(requester: Account): Flow<Token> {
        return tokenRepository.findAllByAccountId(requester.id).map { it.toBusiness() }
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getToken(jwtToken: String): Token? {
        LOGGER.debug("Retrieving token with jwtToken '{}'.", jwtToken)
        val token =
            tokenRepository.findOne(QTokenEntity.tokenEntity.jwtToken.eq(jwtToken)).awaitFirstOrNull()?.toBusiness()
        LOGGER.debug("Token with jwtToken '{}' retrieved.", jwtToken)
        return token
    }

    override suspend fun getAndCheckToken(jwtToken: String): Token {
        LOGGER.debug("Retrieve and check token with jwtToken '{}'.", jwtToken)

        // Check that token has not been revoked.
        val token: Token = tokenRepository.findOne(
            QTokenEntity.tokenEntity.jwtToken.eq(jwtToken)
                .and(QTokenEntity.tokenEntity.revoked.isFalse)
        ).awaitFirstOrNull()?.toBusiness() ?: throw AuthenticationException.RevokedTokenException(jwtToken)
        // Check expiration date.
        token.expirationDate?.let {
            if (it < LocalDate.now()) {
                throw AuthenticationException.ExpiredTokenException(token)
            }
        }
        LOGGER.debug("Token with jwtToken '{}' retrieved and checked.", jwtToken)
        return token
    }

    override suspend fun getTokensByAccountId(accountId: String): Flow<Token> {
        LOGGER.debug("Retrieving all tokens with accountId '{}'.", accountId)
        val predicate = QTokenEntity.tokenEntity.accountId.eq(accountId)
            .and(QTokenEntity.tokenEntity.expirationDate.isNull.or(QTokenEntity.tokenEntity.expirationDate.goe(LocalDate.now())))
            .and(QTokenEntity.tokenEntity.revoked.isFalse)
        val tokens = tokenRepository.findAll(predicate).asFlow().map { it.toBusiness() }
        LOGGER.debug("Retrieved all tokens retrieved for accountId '{}'", accountId)
        return tokens
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun patchToken(requester: Account, tokenId: String, tokenDetails: TokenPatch): Token {
        LOGGER.debug("Patching token with id '{}'.", tokenId)
        val token: TokenEntity =
            tokenRepository.findById(tokenId).awaitFirstOrNull() ?: throw EntityNotFoundException("token", tokenId)
        // Check patch right.
        if (!requester.isAdmin && requester.id != token.accountId) {
            throw IllegalAccessException("Non-admin user ${requester.login} does not have permission to patch others' tokens.")
        }
        val toPatch = token.copy(
            name = tokenDetails.name ?: token.name,
            revoked = tokenDetails.revoked ?: token.revoked
        )
        val updatedToken = tokenRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.debug("Token with id '{}' patched.", tokenId)
        return updatedToken
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun deleteToken(requester: Account, tokenId: String) {
        LOGGER.debug("Deleting token with id '{}'.", tokenId)
        val token: TokenEntity = tokenRepository.findById(tokenId).awaitFirstOrNull()
            ?: throw EntityNotFoundException("token", tokenId)
        // Check deletion right.
        if (!requester.isAdmin && requester.id != token.accountId) {
            throw IllegalAccessException("Non-admin user ${requester.login} does not have permission to delete others' tokens.")
        }
        tokenRepository.delete(token).awaitFirstOrNull()
        LOGGER.debug("Token with id '{}' deleted.", tokenId)
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(TokenServiceImpl::class.java)
    }
}
