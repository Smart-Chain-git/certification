package com.sword.signature.business.visitor

import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.mapper.toModel
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.merkletree.model.TreeElement
import com.sword.signature.merkletree.model.TreeLeaf
import com.sword.signature.merkletree.model.TreeNode
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Tree visitor that persists a tree in database.
 */
class SaveRepositoryTreeVisitor(
    private val jobId: String,
    private val treeElementRepository: TreeElementRepository
) {

    /**
     * Visit a tree and persist its elements in the database.
     * @param rootTreeElement Root of the tree.
     * @return The list of persisted tree elements.
     */
    fun visitTree(rootTreeElement: TreeElement<String>?): Flow<TreeElementEntity> {
        return flow {
            visitTreeElement(rootTreeElement, null, null)
        }
    }

    /**
     * Flow collector which visit each tree element.
     */
    private suspend fun FlowCollector<TreeElementEntity>.visitTreeElement(
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

    private suspend fun FlowCollector<TreeElementEntity>.visitTreeNode(
        treeNode: TreeNode<String>,
        parentId: String?,
        position: TreeElementPosition?
    ) {
        // Node creation.
        val tempTreeElementEntity = TreeElementEntity(
            hash = treeNode.value!!,
            jobId = jobId,
            type = TreeElementType.NODE,
            parentId = parentId,
            position = position
        )
        LOGGER.debug("Insertion of node {}.", tempTreeElementEntity)

        val inserted = treeElementRepository.insert(
            tempTreeElementEntity
        ).awaitSingle()

        // Send back the created node to the caller.
        emit(inserted)

        visitTreeElement(treeNode.left, inserted.id!!, TreeElementPosition.LEFT)
        if (treeNode.right != null) {
            visitTreeElement(treeNode.right!!, inserted.id!!, TreeElementPosition.RIGHT)
        }
    }

    private suspend fun FlowCollector<TreeElementEntity>.visitTreeLeaf(
        treeLeaf: TreeLeaf<String>,
        parentId: String?,
        position: TreeElementPosition?
    ) {
        val metadata = treeLeaf.metadata as FileMetadata
        LOGGER.debug("Insertion of leaf with value {}.", treeLeaf.value)
        val inserted = treeElementRepository.insert(
            TreeElementEntity(
                hash = treeLeaf.value,
                metadata = metadata.toModel(),
                jobId = jobId,
                type = TreeElementType.LEAF,
                parentId = parentId,
                position = position
            )
        ).awaitSingle()

        // Send back the created leaf to the caller.
        emit(inserted)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SaveRepositoryTreeVisitor::class.java)
    }
}
