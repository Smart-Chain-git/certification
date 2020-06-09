package com.sword.signature.merkletree.utils

import org.bouncycastle.util.encoders.Hex
import java.security.MessageDigest

/**
 * Convert a ByteArray to a hexString.
 * @param byteArray ByteArray to convert.
 * @return The HexString representation of the ByteArray.
 */
fun byteArrayToHexString(byteArray: ByteArray): String {
    return Hex.toHexString(byteArray)
}

/**
 * Convert a HexString to a ByteArray.
 * @param hexString HexString to convert.
 * @return The ByteArray representation of the HexString.
 */
fun hexStringToByteArray(hexString: String): ByteArray {
    return Hex.decode(hexString)
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
