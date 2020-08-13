package com.sword.signature.merkletree.visitor

import com.sword.signature.merkletree.model.TreeElement

/**
 * Visitor of a TreeElement used to do operation on the tree.
 * @param T Type of the value stored in tree elements.
 * @param V Return type of visit functions.
 */
interface TreeVisitor<T, V> {

    /**
     * Visit and do some operations on a TreeElement.
     * @param rootTreeElement root TreeElement of the tree to visit.
     * @return Visit result.
     */
    fun visitTree(rootTreeElement: TreeElement<T>?): V
}
