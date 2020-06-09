package com.sword.signature.merkletree.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class HashUtilsTest {
    @Test
    fun byteArrayToHexStringTest() {
        val expected = "2c1d36914b01370735e977"
        val byteArray = byteArrayOf(0x2C, 0x1D, 0x36, 0x91.toByte(), 0x4B, 0x01, 0x37, 0x07, 0x35, 0xE9.toByte(), 0x77)
        val actual = byteArrayToHexString(byteArray)
        assertEquals(expected, actual)
    }

    @Test
    fun hexStringToByteArrayTest() {
        val expected = byteArrayOf(0x2C, 0x56, 0x1F, 0x1D, 0x57)
        val hexString = "2c561f1d57"
        val actual = hexStringToByteArray(hexString)
        for (i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test
    fun hexStringHashFromByteArrayTest() {
        val file = this.javaClass.classLoader.getResource("files/PDF_01.pdf")
        val byteArray = file.readBytes()
        val hash = hexStringHash("SHA-256", byteArray)
        assertEquals("2c1d36914b01370735e97743e2917a961aa6c90ced094f274394a8fc30c6ecd1", hash)
    }

    @Test
    fun hexStringHashFromHexStringTest() {
        val file = this.javaClass.classLoader.getResource("files/PDF_01.pdf")
        val hexString = byteArrayToHexString(file.readBytes())
        val hash = hexStringHash("SHA-256", hexString)
        assertEquals("2c1d36914b01370735e97743e2917a961aa6c90ced094f274394a8fc30c6ecd1", hash)
    }

    @Test
    fun byteArrayHashFromByteArrayTest() {
        val file = this.javaClass.classLoader.getResource("files/PDF_01.pdf")
        val byteArray = file.readBytes()
        val hash = byteArrayHash("SHA-256", byteArray)
        val expected = hexStringToByteArray("2c1d36914b01370735e97743e2917a961aa6c90ced094f274394a8fc30c6ecd1")
        for (i in expected.indices) {
            assertEquals(expected[i], hash[i])
        }
    }

    @Test
    fun byteArrayHashFromHexStringTest() {
        val file = this.javaClass.classLoader.getResource("files/PDF_01.pdf")
        val hexString = byteArrayToHexString(file.readBytes())
        val hash = byteArrayHash("SHA-256", hexString)
        val expected = hexStringToByteArray("2c1d36914b01370735e97743e2917a961aa6c90ced094f274394a8fc30c6ecd1")
        for (i in expected.indices) {
            assertEquals(expected[i], hash[i])
        }
    }
}
