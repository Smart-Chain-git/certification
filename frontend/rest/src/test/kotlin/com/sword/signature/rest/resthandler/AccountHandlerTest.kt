package com.sword.signature.rest.resthandler

import com.sword.signature.business.exception.PasswordTooWeakException
import com.sword.signature.rest.authentication.checkPassword
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AccountHandlerTest(
) {
    @Test
    fun `password too short`() {
        assertThrows<PasswordTooWeakException> {
            checkPassword("S+tr0ng")
        }
    }

    @Test
    fun `password too weak`() {
        assertThrows<PasswordTooWeakException> {
            checkPassword("tooweak01")
        }
    }

    @Test
    fun `password strong`() {
        checkPassword("S+tr0ng!")
    }
}
