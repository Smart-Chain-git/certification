package com.sword.signature.merkletree.utils

import java.security.MessageDigest

/**
 * Convert a ByteArray to a hexString.
 * @param byteArray ByteArray to convert.
 * @return The HexString representation of the ByteArray.
 */
fun byteArrayToHexString(byteArray: ByteArray): String {
    val hexString = StringBuilder(byteArray.size * 2)
    for (byte in byteArray) {
        hexString.append(String.format("%02x", byte))
    }
    return hexString.toString()
}

/**
 * Convert a HexString to a ByteArray.
 * @param hexString HexString to convert.
 * @return The ByteArray representation of the HexString.
 */
fun hexStringToByteArray(hexString: String): ByteArray {
    val byteArray = ByteArray(hexString.length / 2)
    for (i in byteArray.indices) {
        val index = i * 2
        val j = Integer.parseInt(hexString.substring(index, index + 2), 16)
        byteArray[i] = j.toByte()
    }
    return byteArray
}

/**
 * Compute the ByteArray representation of the hash of the ByteArray input.
 * @param algorithm Hash algorithm.
 * @param byteArray ByteArray to hash.
 * @return ByteArray representation of hash.
 */
fun byteArrayHash(algorithm: String, byteArray: ByteArray): ByteArray {
    return MessageDigest.getInstance(algorithm).digest(byteArray)
}

/**
 * Compute the ByteArray representation of the hash of the HexString input.
 * @param algorithm Hash algorithm.
 * @param hexString HexString to hash.
 * @return ByteArray representation of hash.
 */
fun byteArrayHash(algorithm: String, hexString: String): ByteArray {
    return byteArrayHash(algorithm, hexStringToByteArray(hexString))
}

/**
 * Compute the HexString representation of the hash of the ByteArray input.
 * @param algorithm Hash algorithm.
 * @param byteArray ByteArray to hash.
 * @return HexString representation of hash.
 */
fun hexStringHash(algorithm: String, byteArray: ByteArray): String {
    return byteArrayToHexString(byteArrayHash(algorithm, byteArray))
}

/**
 * Compute the HexString representation of the hash of the HexString input.
 * @param algorithm Hash algorithm.
 * @param hexString HexString to hash.
 * @return HexString representation of hash.
 */
fun hexStringHash(algorithm: String, hexString: String): String {
    return byteArrayToHexString(byteArrayHash(algorithm, hexString))
}
