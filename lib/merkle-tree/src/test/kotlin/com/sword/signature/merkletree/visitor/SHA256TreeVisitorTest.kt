package com.sword.signature.merkletree.visitor

import com.sword.signature.merkletree.builder.TreeBuilder
import com.sword.signature.merkletree.generateTestElements
import com.sword.signature.merkletree.getTestElements
import com.sword.signature.merkletree.model.TreeNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import org.slf4j.LoggerFactory
import java.util.stream.Stream


class SHA256TreeVisitorTest {
    @Test
    fun visitRootTest() {
        val merkleTree = TreeBuilder<String>("SHA-256").elements(getTestElements()).build()
        SimpleAlgorithmTreeBrowser("SHA-256").visitTree(merkleTree as TreeNode)
        assertEquals("f4bddde4aec456db7a792c755016ac4d5781bdb004b0c515839ce71181441a25", merkleTree.value)
    }

    @ParameterizedTest
    @MethodSource("hashProvider")
    fun visitRootWithTest(number: Int, expectedRootHash: String) {
        val merkleTree = TreeBuilder<String>("SHA-256").elements(generateTestElements(number)).build()
        val init = System.currentTimeMillis()
        SimpleAlgorithmTreeBrowser("SHA-256").visitTree(merkleTree as TreeNode)
        LOGGER.info("Time to visit tree with {} leaves: {}ms.", number, System.currentTimeMillis() - init)

        assertEquals(expectedRootHash, merkleTree.value)
    }

    fun hashProvider(): Stream<Arguments> {
        val parametre = listOf(
            Arguments.of(100, "d306b461a6899c9d55ba4883159a3dde064646eaca5b7a3b6c488788bf00a4eb"),
            Arguments.of(1_000, "60c1a80fa56be9fa87c8143df0c45761b2c60d642366e216c1e73e9c43b83c4d"),
            Arguments.of(10_000, "6e962a8705e257101ef6b8e13a02a53576f0fdde1034e008a60ed6f3e6fcce6d"),
            Arguments.of(50_000, "603db5d6efc1e658735e2aa853702ddb15053bf50b794c5c3d87a63d889bc4c0"),
            Arguments.of(100_000, "60902d239f3e2b2bf9933f91a864de844d47602483eae0e86f14ab52e83d768a")
        )

        return parametre.stream()
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(SHA256TreeVisitorTest::class.java)
    }
}