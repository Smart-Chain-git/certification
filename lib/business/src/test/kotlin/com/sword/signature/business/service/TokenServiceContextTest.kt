package com.sword.signature.business.service

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.TokenPatch
import com.sword.signature.model.configuration.MongoConfiguration
import com.sword.signature.model.migration.MigrationHandler
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.LocalDate
import java.time.Month

class TokenServiceContextTest @Autowired constructor(
    private val tokenService: TokenService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler : MigrationHandler
) : AbstractServiceContextTest() {

    private val simpleAccount = Account(
        id = "simpleAccountId",
        login = "simple",
        password = "simple",
        email = "simple@signature.com",
        fullName = "Simple",
        isAdmin = false
    )

    private val adminAccount = Account(
        id = "adminAccount",
        login = "admin",
        password = "admin",
        email = "admin@signature.com",
        fullName = "Admin",
        isAdmin = true
    )

    private val token1Id = "5e8b4c28e2018ef99f6a98fe"

    @BeforeAll
    fun initStaticMock() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns LocalDate.of(2020, Month.APRIL, 30)
    }

    @AfterAll
    fun freeStaticMock() {
        unmockkStatic(LocalDate::class)
    }

    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/tokens.json"))
    }

    @Test
    fun createTokenTest() {
        runBlocking {
            val tokenCount = mongoTemplate.getCollection("tokens").awaitSingle().countDocuments().awaitFirst()
            val tokenName = "TestToken"
            val tokenExpirationDate = LocalDate.now().plusDays(180)
            val tokenAccountId = "5e8b48e940fc5793fdcfc716"
            val jwtToken = "pureBulshitString"
            val tokenCreate = TokenCreate(
                name = tokenName,
                expirationDate = tokenExpirationDate,
                accountId = tokenAccountId,
                jwtToken = jwtToken
            )

            val createdToken = tokenService.createToken(tokenCreate)

            assertAll("createdToken",
                { assertEquals(tokenName, createdToken.name) },
                { assertEquals(tokenExpirationDate, createdToken.expirationDate) },
                { assertEquals(tokenAccountId, createdToken.accountId) },
                { assertEquals(jwtToken, createdToken.jwtToken) }
            )
            assertEquals(
                tokenCount + 1,
                mongoTemplate.getCollection("tokens").awaitSingle().countDocuments().awaitFirst()
            )
        }
    }

    @Test
    fun getTokensByAccountId() {
        val tokens = runBlocking { tokenService.getTokensByAccountId("5e8b48e940fc5793fdcfc716").toList() }
        assertEquals(2, tokens.size)
    }

    @Test
    fun patchTokenNameTest() {
        val newTokenName = "newTokenName"
        val tokenDetails = TokenPatch(name = newTokenName)

        val updatedToken = runBlocking {
            tokenService.patchToken(
                requester = adminAccount,
                tokenId = token1Id,
                tokenDetails = tokenDetails
            )
        }

        assertAll("updatedToken",
            { assertEquals(token1Id, updatedToken.id) },
            { assertEquals(newTokenName, updatedToken.name) }
        )
    }

    @Test
    fun revokeTokenTest() {
        val tokenDetails = TokenPatch(revoked = true)

        val updatedToken = runBlocking {
            tokenService.patchToken(
                requester = adminAccount,
                tokenId = token1Id,
                tokenDetails = tokenDetails
            )
        }

        assertAll("updatedToken",
            { assertEquals(token1Id, updatedToken.id) },
            { assertTrue(updatedToken.revoked) }
        )
    }

    @Test
    fun patchNonexistentTokenTest() {
        val tokenDetails = TokenPatch(name = "Yolo")

        assertThrows<EntityNotFoundException> {
            runBlocking {
                tokenService.patchToken(
                    requester = adminAccount,
                    tokenId = "nonexistentId",
                    tokenDetails = tokenDetails
                )
            }
        }
    }

    @Test
    fun patchOtherTokenAsNonAdminTest() {
        val tokenDetails = TokenPatch(name = "Yolo")

        assertThrows<IllegalAccessException> {
            runBlocking {
                tokenService.patchToken(
                    requester = simpleAccount,
                    tokenId = token1Id,
                    tokenDetails = tokenDetails
                )
            }
        }
    }

    @Test
    fun deleteTokenTest() {
        runBlocking {
            val tokenCount = mongoTemplate.getCollection("tokens").awaitSingle().countDocuments().awaitFirst()
            tokenService.deleteToken(adminAccount, token1Id)
            assertEquals(tokenCount - 1, mongoTemplate.getCollection("tokens").awaitSingle().countDocuments().awaitFirst())
        }
    }

    @Test
    fun deleteNonexistentTokenTest() {
        assertThrows<EntityNotFoundException> {
            runBlocking { tokenService.deleteToken(adminAccount, "nonexistentId") }
        }
    }

    @Test
    fun deleteOtherTokenAsNonAdminTest() {
        assertThrows<IllegalAccessException> {
            runBlocking { tokenService.deleteToken(simpleAccount, token1Id) }
        }
    }
}