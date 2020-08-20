package com.sword.signature.business.model

data class MerkelTree(
    val algorithm: String,
    val root: Node
) {
    data class Node(
        val hash: String,
        var left: Node?=null,
        var right: Node?=null
    )
}
