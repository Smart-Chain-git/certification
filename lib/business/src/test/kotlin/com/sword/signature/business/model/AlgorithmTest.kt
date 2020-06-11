package com.sword.signature.business.model

import com.sword.signature.merkletree.utils.hexStringToByteArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AlgorithmTest {

    @ParameterizedTest
    @MethodSource("goodHexStringHashes")
    fun `Test checkHashDigest with good hexString hashes`(algorithm: Algorithm, hash: String) {
        Assertions.assertTrue(algorithm.checkHashDigest(hash))
    }

    @ParameterizedTest
    @MethodSource("goodByteArrayHashes")
    fun `Test checkHashDigest with good byteArray hashes`(algorithm: Algorithm, hash: ByteArray) {
        Assertions.assertTrue(algorithm.checkHashDigest(hash))
    }

    @ParameterizedTest
    @MethodSource("badHexStringHashes")
    fun `Test checkHashDigest with bad hexString hashes`(algorithm: Algorithm, hash: String) {
        Assertions.assertFalse(algorithm.checkHashDigest(hash))
    }

    @ParameterizedTest
    @MethodSource("badByteArrayHashes")
    fun `Test checkHashDigest with bad byteArray hashes`(algorithm: Algorithm, hash: ByteArray) {
        Assertions.assertFalse(algorithm.checkHashDigest(hash))
    }

    companion object {
        private val md5 = Algorithm("5e7ce2379d1d9dd15b4d70f6", "MD5", 16)
        private val sha1 = Algorithm("5e7ce18e36eb55101ed9d9ed", "SHA-1", 20)
        private val sha256 = Algorithm("5e7ce0c818f1f6a462448c06", "SHA-256", 32)

        @JvmStatic
        fun goodHexStringHashes() = listOf(
                Arguments.of(md5, "098f6bcd4621d373cade4e832627b4f6"),
                Arguments.of(sha1, "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3"),
                Arguments.of(sha256, "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
        )

        @JvmStatic
        fun goodByteArrayHashes() = listOf(
                Arguments.of(md5, hexStringToByteArray("098f6bcd4621d373cade4e832627b4f6")),
                Arguments.of(sha1, hexStringToByteArray("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3")),
                Arguments.of(sha256, hexStringToByteArray("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
        )

        @JvmStatic
        fun badHexStringHashes() = listOf(
                Arguments.of(md5, "098f6bcd4621d373cade4e83"),
                Arguments.of(md5, "098f6bcd4621d373cyde4e832627b4f6"),
                Arguments.of(sha1, "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3ef56"),
                Arguments.of(sha1, "a94a8fe5ccb19baghc4c0873d391e987982fbbd3"),
                Arguments.of(sha256, "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f"),
                Arguments.of(sha256, "9f86d081884c7d659a2feaa0c55aq015a3bf4f1b2b0b822cd15d6c15b0f00a08")
        )

        @JvmStatic
        fun badByteArrayHashes() = listOf(
                Arguments.of(md5, hexStringToByteArray("098f6bcd4621d373cade4e83")),
                Arguments.of(sha1, hexStringToByteArray("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3ef56")),
                Arguments.of(sha256, hexStringToByteArray("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f0"))
        )
    }
}