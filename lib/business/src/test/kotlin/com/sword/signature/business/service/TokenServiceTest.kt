package com.sword.signature.business.service

import com.sword.signature.business.model.TokenDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenServiceTest {

    private val tokenService = TokenService("Yolo@Signature", "Tezos Signature")

    @Test
    fun generateAdminToken() {
        val token = tokenService.createToken(TokenDetails(
                id = "5e74a073a386f170f3850b4b",
                login = "admin"
        ))
        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUZXpvcyBTaWduYXR1cmUiLCJpZCI6IjVlNzRhMDczYTM4NmYxNzBmMzg1MGI0YiIsImxvZ2luIjoiYWRtaW4ifQ.-rGC3TXC-GQxVjnW6iSyQsFnRmxL_NIe5tkstPaAul0", token)
    }
}