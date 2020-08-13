package com.sword.signature.merkletree.model

/**
 * Element of a merkle tree.
 * @param T Type of the value stored in each tree element.
 */
abstract class TreeElement<T> {
    /**
     * Value stored in the element.
     */
    abstract val value: T?
    /**
     * State of root.
     */
    abstract val isRoot: Boolean
}
