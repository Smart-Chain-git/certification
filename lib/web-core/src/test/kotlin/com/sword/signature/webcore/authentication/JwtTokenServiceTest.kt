package com.sword.signature.webcore.authentication

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

class JwtTokenServiceTest {


    private val jwtTokenService = JwtTokenService("ffe50f21d8359de7245dc13777812c2a", "Tezos@Signature")
    private val creationTime = OffsetDateTime.of(2020, 1, 3, 13, 30, 0, 0, ZoneOffset.UTC)
    private val expirationTime = OffsetDateTime.of(2020, 1, 3, 15, 30, 0, 0, ZoneOffset.UTC)

    @BeforeAll
    fun initMock() {
        mockkStatic(OffsetDateTime::class)
    }

    @AfterAll
    fun freeStaticMocks() {
        unmockkStatic(OffsetDateTime::class)
    }


    @Test
    fun `generate and decode persistent Token`() {

        every { OffsetDateTime.now() } returns creationTime

        val accountId = "uberId"
        val token = jwtTokenService.generatePersistantToken(accountId)
        val actual = jwtTokenService.parseToken(token)

        val expected = LoginTokenInfo(id = accountId, persisted = true, creationTime = creationTime, expirationTime = null, login = "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `generate and decode volatile Token`() {

        every { OffsetDateTime.now() } returns creationTime

        val accountId = "bofId"
        val token = jwtTokenService.generateVolatileToken(accountId, Duration.ofHours(2))
        val actual = jwtTokenService.parseToken(token)

        val expected = LoginTokenInfo(id = accountId, persisted = false, creationTime = creationTime, expirationTime = expirationTime, login = "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
