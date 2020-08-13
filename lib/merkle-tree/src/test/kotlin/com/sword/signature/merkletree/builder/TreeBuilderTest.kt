package com.sword.signature.merkletree.builder

import com.sword.signature.merkletree.generateTestElements
import com.sword.signature.merkletree.getTestElements
import com.sword.signature.merkletree.model.TreeElement
import com.sword.signature.merkletree.model.TreeNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.slf4j.LoggerFactory
import java.util.stream.Stream

class TreeBuilderTest {
    @Test
    fun buildTest() {
        val elements = getTestElements()
        val merkleTree: TreeElement<String>? = TreeBuilder<String>("SHA-256").elements(elements).build()
        assertTrue(merkleTree is TreeNode)
        assertTrue((merkleTree as TreeNode).height == 4)
    }


    @ParameterizedTest
    @MethodSource("sizeProvider")
    fun buildWithTest(number: Int, expectedHeight: Int) {
        val elements = generateTestElements(number)
        val init = System.currentTimeMillis()
        val merkleTree: TreeElement<String>? = TreeBuilder<String>("SHA-256").elements(elements).build()
        LOGGER.info("Time to build tree with {} leaves: {}ms.", number, System.currentTimeMillis() - init)
        assertTrue(merkleTree is TreeNode)
        assertEquals(expectedHeight, (merkleTree as TreeNode).height)
    }

    fun sizeProvider(): Stream<Arguments> {
        val parameters = listOf(
            Arguments.of(100, 7),
            Arguments.of(1_000, 10),
            Arguments.of(10_000, 14),
            Arguments.of(50_000, 16),
            Arguments.of(100_000, 17),
            Arguments.of(1_000_000, 20)
        )

        return parameters.stream()
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(TreeBuilderTest::class.java)
    }
}
