package com.sword.signature.business.service

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.model.JwtTokenDetails
import com.sword.signature.business.model.Token
import com.sword.signature.business.service.impl.TokenServiceImpl
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
    private val tokenService = TokenServiceImpl("ffe50f21d8359de7245dc13777812c2a", "Tezos@Signature", tokenRepository)

    private val tokenId = "tokenId"
    private val accountId = "accountId"
    private val jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGlvblRpbWUiOiJjcmVhdGlvblRpbWUiLCJpc3MiOiJUZXpvc0BTaWduYXR1cmUiLCJpZCI6ImFjY291bnRJZCJ9.zWQal4cCAEE4M0iJktv9VrklQRiZlL22DmVNo6YEiAY"
    private val jwtTokenDetails = JwtTokenDetails(
            id = accountId,
            creationTime = "creationTime"
    )

    private val expirationDate = LocalDate.of(2020, Month.APRIL, 3)
    private val validDate = LocalDate.of(2020, Month.MARCH, 20)
    private val expiredDate = LocalDate.of(2020, Month.MAY, 1)

    private val tokenEntity = TokenEntity(tokenId, jwtToken, expirationDate, accountId)
    private val token = Token(tokenId, jwtToken, expirationDate, accountId)


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
        coEvery { tokenRepository.findByJwtToken(jwtToken) } returns Mono.just(tokenEntity)
        assertEquals(token, runBlocking { tokenService.getToken(jwtToken) })
    }

    @Test
    fun checkAndGetTokenToken() {
        coEvery { tokenRepository.findByJwtToken(jwtToken) } returns Mono.just(tokenEntity)
        every { LocalDate.now() } returns validDate
        assertEquals(token, runBlocking { tokenService.checkAndGetToken(jwtToken) })
    }

    @Test
    fun checkAndGetRevokedTokenTest() {
        coEvery { tokenRepository.findByJwtToken(jwtToken) } returns Mono.empty()
        assertThrows<AuthenticationException.RevokedTokenException> { runBlocking { tokenService.checkAndGetToken(jwtToken) } }
    }

    @Test
    fun checkAndGetExpiredTokenTest() {
        coEvery { tokenRepository.findByJwtToken(jwtToken) } returns Mono.just(tokenEntity)
        every { LocalDate.now() } returns expiredDate
        assertThrows<AuthenticationException.ExpiredTokenException> { runBlocking { tokenService.checkAndGetToken(jwtToken) } }
    }

    @Test
    fun createTokenTest() {
        assertEquals(jwtToken, tokenService.createToken(jwtTokenDetails))
    }

    @Test
    fun parseTokenTest() {
        assertEquals(jwtTokenDetails, tokenService.parseToken(jwtToken))
    }
}