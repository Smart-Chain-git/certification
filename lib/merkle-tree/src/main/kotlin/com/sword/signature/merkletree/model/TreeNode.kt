package com.sword.signature.merkletree.model

/**
 * Node of a merkle tree.
 * @param T Type of the value stored in the node.
 */
data class TreeNode<T>(
    /**
     * Value stored in the node
     */
    override var value: T?,

    /**
     * State of root.
     */
    override val isRoot: Boolean,

    /**
     * Height of the node.
     * 0 being the height of a leaf.
     */
    val height: Int,
    /**
     * Left child of the node.
     * Always defined.
     */
    val left: TreeElement<T>,
    /**
     * Right child of the node.
     * Can be undefined.
     */
    val right: TreeElement<T>?
) : TreeElement<T>()
