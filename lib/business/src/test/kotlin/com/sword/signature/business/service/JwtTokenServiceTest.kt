package com.sword.signature.business.service

import com.sword.signature.business.model.JwtTokenDetails
import com.sword.signature.business.service.impl.JwtTokenServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JwtTokenServiceTest {

    private val jwtTokenService = JwtTokenServiceImpl("ffe50f21d8359de7245dc13777812c2a", "Tezos@Signature")

    private val jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGlvblRpbWUiOiIyMDIwLTA0LTA2VDAwOjQxOjI4LjkxMjA4OCIsImlzcyI6IlRlem9zQFNpZ25hdHVyZSIsImlkIjoiNWU3NGEwNzNhMzg2ZjE3MGYzODUwYjRiIn0.0I9nxqktj6-c2mwMR0C5Jxa0idIyQFQn4qBGD1gVVAQ"
    private val jwtTokenDetails = JwtTokenDetails(
            id = "5e74a073a386f170f3850b4b",
            creationTime = "2020-04-06T00:41:28.912088"
    )

    @Test
    fun createTokenTest() {
        assertEquals(jwtToken, jwtTokenService.createToken(jwtTokenDetails))
    }

    @Test
    fun parseTokenTest() {
        assertEquals(jwtTokenDetails, jwtTokenService.parseToken(jwtToken))
    }
}