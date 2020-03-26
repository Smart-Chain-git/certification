package com.sword.signature.business.service;

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.security.MessageDigest

class AlgorithmServiceContextTest @Autowired constructor(
        private val algorithmService: AlgorithmService,
        override val mongoTemplate: ReactiveMongoTemplate
) : AbstractServiceContextTest() {

    @Test
    fun `find MD5 algorithm`() = findAlgorithmByNameTest("MD5", 16)

    @Test
    fun `find SHA-1 algorithm`() = findAlgorithmByNameTest("SHA-1", 20)

    @Test
    fun `find SHA-256 algorithm`() = findAlgorithmByNameTest("SHA-256", 32)

    @Test
    fun `find SHA-384 algorithm`() = findAlgorithmByNameTest("SHA-384", 48)

    @Test
    fun `find SHA-512 algorithm`() = findAlgorithmByNameTest("SHA-512", 64)

    fun findAlgorithmByNameTest(algorithmName: String, expectedDigestLength: Int) {
        val algorithm = runBlocking { algorithmService.getAlgorithmByName(algorithmName) }
        assertAll("algorithm",
                { assertEquals(algorithmName, algorithm?.name) },
                { assertEquals(expectedDigestLength, algorithm?.digestLength) },
                { assertEquals(expectedDigestLength, MessageDigest.getInstance(algorithmName).digestLength) }
        )
    }
}
