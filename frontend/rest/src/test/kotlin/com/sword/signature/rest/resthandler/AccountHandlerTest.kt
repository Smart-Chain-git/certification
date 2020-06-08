package com.sword.signature.rest.resthandler

import com.sword.signature.business.exception.PasswordTooWeakException
import com.sword.signature.rest.PasswordChecker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AccountHandlerTest (
) {
    @Test
    fun `password too short`() {
        assertThrows<PasswordTooWeakException> {
            runBlocking {
                PasswordChecker.checkPassword("S+tr0ng")
            }
        }
    }

    @Test
    fun `password too weak`() {
        assertThrows<PasswordTooWeakException> {
            runBlocking {
                PasswordChecker.checkPassword("tooweak01")
            }
        }
    }

    @Test
    fun `password strong`() {
        runBlocking {
            PasswordChecker.checkPassword("S+tr0ng!")
        }
    }
}
