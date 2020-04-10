package com.sword.signature.business.service

import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.service.impl.TokenServiceImpl
import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.LocalDate

class TokenServiceContextTest @Autowired constructor(
        private val tokenService: TokenService,
        override val mongoTemplate: ReactiveMongoTemplate,
        override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {

    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/tokens.json"))
    }

    @Test
    fun createTokenTest() {
        val tokenCount = runBlocking { mongoTemplate.getCollection("tokens").countDocuments().awaitFirst() }
        val tokenName = "TestToken"
        val tokenExpirationDate = LocalDate.now().plusDays(180)
        val tokenAccountId = "5e8b48e940fc5793fdcfc716"
        val tokenCreate = TokenCreate(
                name = tokenName,
                expirationDate = tokenExpirationDate,
                accountId = tokenAccountId
        )

        val createdToken = runBlocking { tokenService.createToken(tokenCreate) }

        assertAll("createdToken",
                { assertEquals(tokenName, createdToken.name) },
                { assertEquals(tokenExpirationDate, createdToken.expirationDate) },
                { assertEquals(tokenAccountId, createdToken.accountId) },
                { assertEquals(tokenAccountId, (tokenService as TokenServiceImpl).parseToken(createdToken.jwtToken).id) }
        )
        assertEquals(tokenCount + 1, runBlocking { mongoTemplate.getCollection("tokens").countDocuments().awaitFirst() })
    }

    @Test
    fun getTokensByAccountId() {
        val tokens = runBlocking { tokenService.getTokensByAccountId("5e8b48e940fc5793fdcfc716").toList() }
        assertEquals(3, tokens.size)
    }


}