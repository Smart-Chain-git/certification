package com.sword.signature.business.service

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.model.Token
import com.sword.signature.business.service.impl.TokenServiceImpl
import com.sword.signature.model.entity.QTokenEntity
import com.sword.signature.model.entity.TokenEntity
import com.sword.signature.model.repository.TokenRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.Month

class TokenServiceTest {

    private val tokenRepository: TokenRepository = mockk()
    private val tokenService = TokenServiceImpl(tokenRepository)

    private val tokenId = "tokenId"
    private val tokenName = "tokenName"
    private val accountId = "accountId"
    private val jwtToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGlvblRpbWUiOiJjcmVhdGlvblRpbWUiLCJpc3MiOiJUZXpvc0BTaWduYXR1cmUiLCJpZCI6ImFjY291bnRJZCJ9.zWQal4cCAEE4M0iJktv9VrklQRiZlL22DmVNo6YEiAY"
    private val expirationDate = LocalDate.of(2020, Month.APRIL, 3)
    private val validDate = LocalDate.of(2020, Month.MARCH, 20)

    private val creationDate = LocalDate.of(1978, Month.FEBRUARY, 25)

    private val expiredDate = LocalDate.of(2020, Month.MAY, 1)

    private val tokenEntity = TokenEntity(
        id = tokenId, name = tokenName, jwtToken = jwtToken,
        expirationDate = expirationDate, accountId = accountId
    )
    private val token = Token(
        id = tokenId,
        name = tokenName,
        jwtToken = jwtToken,
        creationDate = creationDate,
        expirationDate = expirationDate,
        accountId = accountId,
        revoked = false
    )


    @BeforeAll
    fun initMock() {
        mockkStatic(LocalDate::class)
    }

    @AfterAll
    fun freeStaticMocks() {
        unmockkStatic(LocalDate::class)
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(tokenRepository)
    }

    @Test
    fun getTokenTest() {
        coEvery { tokenRepository.findOne(QTokenEntity.tokenEntity.jwtToken.eq(jwtToken)) } returns Mono.just(
            tokenEntity
        )
        assertEquals(token, runBlocking { tokenService.getToken(jwtToken) })
    }

    @Test
    fun getAndCheckTokenToken() {
        coEvery {
            tokenRepository.findOne(
                QTokenEntity.tokenEntity.jwtToken.eq(jwtToken)
                    .and(QTokenEntity.tokenEntity.revoked.isFalse)
            )
        } returns Mono.just(
            tokenEntity
        )
        every { LocalDate.now() } returns validDate
        assertEquals(token, runBlocking { tokenService.getAndCheckToken(jwtToken) })
    }

    @Test
    fun getAndCheckRevokedTokenTest() {
        coEvery {
            tokenRepository.findOne(
                QTokenEntity.tokenEntity.jwtToken.eq(jwtToken).and(QTokenEntity.tokenEntity.revoked.isFalse)
            )
        } returns Mono.empty()
        assertThrows<AuthenticationException.RevokedTokenException> {
            runBlocking {
                tokenService.getAndCheckToken(
                    jwtToken
                )
            }
        }
    }

    @Test
    fun getAndCheckExpiredTokenTest() {
        coEvery {
            tokenRepository.findOne(
                QTokenEntity.tokenEntity.jwtToken.eq(jwtToken).and(QTokenEntity.tokenEntity.revoked.isFalse)
            )
        } returns Mono.just(
            tokenEntity
        )
        every { LocalDate.now() } returns expiredDate
        assertThrows<AuthenticationException.ExpiredTokenException> {
            runBlocking {
                tokenService.getAndCheckToken(
                    jwtToken
                )
            }
        }
    }
}