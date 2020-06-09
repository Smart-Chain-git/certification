package com.sword.signature.merkletree.builder

import com.sword.signature.merkletree.model.TreeElement
import com.sword.signature.merkletree.model.TreeLeaf
import com.sword.signature.merkletree.model.TreeNode
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.ln

/**
 * Provides a way to easily build merkle tree by passing a list of elements to add.
 * @param T Type of value stored in the tree.
 */
class TreeBuilder<T>(
    /**
     * Algorithm used to produce leaves values.
     */
    private val algorithm: String
) {
    private var elements: List<Pair<T, Any>>? = null

    /**
     * Add the elements map to be used by the builder to build the tree.
     * @param elements Map of elements with an element being :
     * - key: id of the element, used as the value in the leaves;
     * - value: info on the element, stored as metadata in the leaves.
     * @return The TreeBuilder with the elements added.
     */
    fun elements(elements: List<Pair<T, Any>>): TreeBuilder<T> = apply { this.elements = elements }

    /**
     * Build the tree corresponding to the elements provided.
     * @return The representation of the elements as a TreeElement if the list is not empty.
     * Notice that if there is only one element, a TreeLeaf is returned, else it returns a TreeNode.
     */
    fun build(): TreeElement<T>? {

        if(elements == null) {
            throw IllegalArgumentException("Cannot build with null elements")
        }

        return when (elements!!.size) {
            0 -> null
            1 -> TreeLeaf(
                value = elements!!.elementAt(0).first,
                isRoot = true,
                algorithm = algorithm,
                metadata = elements!!.elementAt(0).second
            )
            else -> {
                val depth = ceil(ln(elements!!.size.toDouble()) / ln(2.0)).toInt()
                return createNodes(depth)
            }
        }
    }

    /**
     * Build a TreeNode using the depth.
     * @param depth Depth of the node in the tree (depth of 0 representing a leaf)
     */
    private fun createNodes(depth: Int): TreeNode<T> {

        //creation de toutes les feuilles
        var intermediary: List<TreeElement<T>> = elements!!.map {
            TreeLeaf(value = it.first, isRoot = false, algorithm = algorithm, metadata = it.second)
        }

        (1..depth).forEach { currentDepth ->
            //regroupement de tout les element par deux
            intermediary = intermediary.chunked(2) {
                TreeNode(
                    value = null,
                    isRoot = currentDepth == depth,
                    height = currentDepth,
                    left = it.elementAt(0),
                    right = it.elementAtOrNull(1)
                )
            }
        }

        return intermediary[0] as TreeNode<T>
    }

}