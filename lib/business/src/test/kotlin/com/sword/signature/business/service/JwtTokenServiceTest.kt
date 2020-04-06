package com.sword.signature.business.service

import com.sword.signature.business.model.TokenDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class JwtTokenServiceTest {

    private val tokenService = JwtTokenService("ffe50f21d8359de7245dc13777812c2a", "Tezos@Signature")

    @Test
    fun generateAdminToken() {
        val token = tokenService.createToken(TokenDetails(
                id = "5e74a073a386f170f3850b4b",
                creationTime = "2020-04-06T00:41:28.912088"
        ))
        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGlvblRpbWUiOiIyMDIwLTA0LTA2VDAwOjQxOjI4LjkxMjA4OCIsImlzcyI6IlRlem9zQFNpZ25hdHVyZSIsImlkIjoiNWU3NGEwNzNhMzg2ZjE3MGYzODUwYjRiIn0.0I9nxqktj6-c2mwMR0C5Jxa0idIyQFQn4qBGD1gVVAQ", token)
    }
}