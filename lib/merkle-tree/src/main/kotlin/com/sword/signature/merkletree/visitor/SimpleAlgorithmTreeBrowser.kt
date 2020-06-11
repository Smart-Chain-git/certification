package com.sword.signature.merkletree.visitor

import com.sword.signature.merkletree.model.TreeElement
import com.sword.signature.merkletree.model.TreeLeaf
import com.sword.signature.merkletree.model.TreeNode
import com.sword.signature.merkletree.utils.hexStringHash

/**
 * Abstract visitor of a merkle tree.
 * It computes the hash of a node by hashing the concatenation of the children values recursively along the tree.
 * The hash function must be set in the implementation.
 */
class SimpleAlgorithmTreeBrowser(
    private val algorithm: String
) : TreeVisitor<String, Unit> {


    override fun visitTree(rooTreeElement: TreeElement<String>?)  {
        if(rooTreeElement!=null) {
            visitTreeElement(rooTreeElement)
        }
    }

    private fun visitTreeElement(treeElement: TreeElement<String>): String {

        return if (treeElement is TreeNode) {
            visitTreeNode(treeElement)
        } else {
            visitTreeLeaf(treeElement as TreeLeaf)
        }
    }

    /**
     * Visit a TreeNode, compute, and set its value.
     * The value is the hash of the concatenation of the children values.
     * @param treeNode TreeNode to visit.
     */
    private fun visitTreeNode(treeNode: TreeNode<String>): String {
        // Retrieve the left child value.
        var sum: String = visitTreeElement(treeNode.left)
        // Check if there is a right child.
        if (treeNode.right != null) {
            sum += visitTreeElement(treeNode.right)
        }
        // Compute the hash of the concatenation.
        val hash = hexStringHash(algorithm, sum)
        treeNode.value = hash
        return hash
    }

    /**
     * Visit a TreeLeaf.
     * @param treeLeaf TreeLeaf to visit.
     */
    private fun visitTreeLeaf(treeLeaf: TreeLeaf<String>): String {
        return treeLeaf.value
    }
}