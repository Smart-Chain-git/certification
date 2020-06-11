package com.sword.signature.merkletree.model


/**
 * Leaf of a merkle tree.
 * @param T Type of the value stored in the leaf.
 */
data class TreeLeaf<T>(
    /**
     * Value stored in the leaf.
     */
    override val value: T,
    /**
     * State of root.
     */
    override val isRoot: Boolean,
    /**
     * Algorithm used to hash the leaf value.
     */
    val algorithm: String,

    /**
     * Information relative to the value stored in the leaf.
     */
    var metadata: Any? = null
) : TreeElement<T>()
