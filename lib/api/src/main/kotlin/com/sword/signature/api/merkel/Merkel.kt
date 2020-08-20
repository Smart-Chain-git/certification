package com.sword.signature.api.merkel

import com.fasterxml.jackson.annotation.JsonInclude

data class MerkelTree(
    val algorithm: String,
    val root: Node
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Node(
        val hash: String,
        val left: Node?,
        val right: Node?
    )
}
