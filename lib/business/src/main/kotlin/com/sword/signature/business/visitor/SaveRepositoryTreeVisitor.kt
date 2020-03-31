package com.sword.signature.business.visitor

import com.sword.signature.business.model.FileHash
import com.sword.signature.merkletree.model.TreeElement
import com.sword.signature.merkletree.model.TreeLeaf
import com.sword.signature.merkletree.model.TreeNode
import com.sword.signature.merkletree.visitor.TreeVisitor
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.entity.TreeElementPosition
import com.sword.signature.model.entity.TreeElementType
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking

class SaveRepositoryTreeVisitor(
    private val jobId: String,
    private val treeElementRepository: TreeElementRepository
) : TreeVisitor<String, Unit> {

    override fun visitTree(rooTreeElement: TreeElement<String>?) {
        runBlocking {
            visitTreeElement(rooTreeElement, null, null)
        }
    }


    private suspend fun visitTreeElement(
        treeElement: TreeElement<String>?,
        parentId: String?,
        position: TreeElementPosition?
    ) {
        if (treeElement is TreeNode) {
            visitTreeNode(treeElement, parentId, position)
        } else {
            visitTreeLeaf(treeElement as TreeLeaf, parentId, position)
        }
    }

    private suspend fun visitTreeNode(treeNode: TreeNode<String>, parentId: String?, position: TreeElementPosition?) {
        //creation entité
        val nodeEntity = treeElementRepository.save(
            TreeElementEntity(
                hash = treeNode.value!!,
                jobId = jobId,
                type = TreeElementType.NODE,
                parentId = parentId,
                position = position
            )
        ).awaitSingle()

        visitTreeElement(treeNode.left, nodeEntity.id, TreeElementPosition.LEFT)
        if (treeNode.right != null) {
            visitTreeElement(treeNode.right!!, nodeEntity.id, TreeElementPosition.RIGHT)
        }
    }

    private suspend fun visitTreeLeaf(treeLeaf: TreeLeaf<String>, parentId: String?, position: TreeElementPosition?) {
        val metadata=treeLeaf.metadata as FileHash
        treeElementRepository.save(
            TreeElementEntity(
                hash = metadata.hash,
                fileName = metadata.fileName,
                jobId = jobId,
                type = TreeElementType.LEAF,
                parentId = parentId,
                position = position
            )
        ).awaitSingle()
    }


}