package com.sword.signature.business.model

data class Algorithm(
        val id: String,
        val name: String,
        val digestLength: Int
) {
    fun checkHashDigest(hexString: String): Boolean {
        return hexString.length == digestLength * 2 && hexString.matches(Regex("[0-9a-fA-F]+"))
    }

    fun checkHashDigest(byteArray: ByteArray): Boolean {
        return byteArray.size == digestLength
    }
}
