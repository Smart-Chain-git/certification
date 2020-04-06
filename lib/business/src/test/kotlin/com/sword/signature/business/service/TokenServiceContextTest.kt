package com.sword.signature.business.service

import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path

class TokenServiceContextTest @Autowired constructor(
        private val tokenService: TokenService,
        override val mongoTemplate: ReactiveMongoTemplate,
        override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {
    private val tokenCount = 3

    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/tokens.json"))
    }

    @Test
    fun getTokensByAccountId() {
        val tokens = runBlocking { tokenService.getTokensByAccountId("5e8b48e940fc5793fdcfc716").toList() }
        assertEquals(3, tokens.size)
    }


}