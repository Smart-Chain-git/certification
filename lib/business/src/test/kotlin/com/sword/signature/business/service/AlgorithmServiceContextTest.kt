package com.sword.signature.business.service;

import com.sword.signature.business.exception.AlgorithmNotFoundException
import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.security.MessageDigest

class AlgorithmServiceContextTest @Autowired constructor(
        private val algorithmService: AlgorithmService,
        override val mongoTemplate: ReactiveMongoTemplate,
        override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {

    @BeforeAll
    fun initDatabase() {
        resetDatabase()
    }

    @ParameterizedTest
    @MethodSource("algorithms", "lowerCaseAlgorithms")
    fun findAlgorithmByNameTest(algorithmName: String, expectedDigestLength: Int) {
        val algorithm = runBlocking { algorithmService.getAlgorithmByName(algorithmName) }
        assertAll("algorithm",
                { assertEquals(algorithmName.toUpperCase(), algorithm.name) },
                { assertEquals(expectedDigestLength, algorithm.digestLength) },
                { assertEquals(expectedDigestLength, MessageDigest.getInstance(algorithmName).digestLength) }
        )
    }

    @Test
    fun findAlgorithmByNameExceptionTest() {
        assertThrows<AlgorithmNotFoundException> { runBlocking { algorithmService.getAlgorithmByName("non_existing") } }
    }

    companion object {
        @JvmStatic
        fun algorithms() = listOf(
                Arguments.of("MD5", 16),
                Arguments.of("SHA-1", 20),
                Arguments.of("SHA-256", 32),
                Arguments.of("SHA-384", 48),
                Arguments.of("SHA-512", 64)
        )

        @JvmStatic
        fun lowerCaseAlgorithms() = algorithms().map { arg -> Arguments.of((arg.get()[0] as String).toLowerCase(), arg.get()[1]) }
    }

}
