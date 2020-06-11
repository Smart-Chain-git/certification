package com.sword.signature.merkletree

import com.sword.signature.merkletree.utils.hexStringHash
import java.io.File
import java.nio.ByteBuffer

fun getTestElements(): List<Pair<String, Any>> {
    val folder: File = File("src/test/resources/files")
    val files = folder.listFiles()
    files.sort()

    return files.map {
        val hash: String = hexStringHash("SHA-256", it.readBytes())
        Pair(hash,it.name)
    }

}

fun generateTestElements(number: Int): List<Pair<String, Any>> {
    return (0 until number).map{
        val toHash = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(it).array()
        val hash = hexStringHash("SHA-256", toHash)
        Pair(hash,toHash)
    }
}