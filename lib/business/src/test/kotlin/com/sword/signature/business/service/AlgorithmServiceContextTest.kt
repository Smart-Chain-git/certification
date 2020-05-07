package com.sword.signature.business.service;

import com.sword.signature.business.exception.AlgorithmNotFoundException
import com.sword.signature.business.model.Algorithm
import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
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

    private val algorithms = listOf(
        Algorithm(
            id = "5e7ce2379d1d9dd15b4d70f6",
            name = "MD5",
            digestLength = 16
        ),
        Algorithm(
            id = "5e7ce18e36eb55101ed9d9ed",
            name = "SHA-1",
            digestLength = 20
        ),
        Algorithm(
            id = "5e7ce0c818f1f6a462448c06",
            name = "SHA-256",
            digestLength = 32
        ),
        Algorithm(
            id = "5e7ce0e3c3ae3d5cb662944b",
            name = "SHA-384",
            digestLength = 48
        ),
        Algorithm(
            id = "5e7ce0ec5135e4b01d8eebc2",
            name = "SHA-512",
            digestLength = 64
        )
    )

    @Test
    fun findAll() {
        runBlocking {
            val actual = algorithmService.findAll().toList()
            Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(algorithms)

        }
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
        fun lowerCaseAlgorithms() =
            algorithms().map { arg -> Arguments.of((arg.get()[0] as String).toLowerCase(), arg.get()[1]) }
    }

}
