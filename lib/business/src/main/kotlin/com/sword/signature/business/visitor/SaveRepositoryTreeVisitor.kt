package com.sword.signature.business.visitor


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

class SaveRepositoryTreeVisitor(
    private val jobId: String,
    private val treeElementRepository: TreeElementRepository
) {

    /**
     * visite l'arbre et retourne un flow d'element créé dans la BDD
     */

    fun visitTree(rooTreeElement: TreeElement<String>?): Flow<TreeElementEntity> {
        return flow {
            visitTreeElement(rooTreeElement, null, null)
        }
    }


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

    private suspend fun FlowCollector<TreeElementEntity>.visitTreeNode(treeNode: TreeNode<String>, parentId: String?, position: TreeElementPosition?) {
        //creation entité
        val tempTreeElementEntity = TreeElementEntity(
            hash = treeNode.value!!,
            jobId = jobId,
            type = TreeElementType.NODE,
            parentId = parentId,
            position = position
        )
        LOGGER.trace("ecriture node {}", tempTreeElementEntity)


        val inserted = treeElementRepository.insert(
            tempTreeElementEntity
        ).awaitSingle()
        //on renvoi le noeud cree à l'appeleur
        emit (inserted)

        visitTreeElement(treeNode.left, inserted.id!!, TreeElementPosition.LEFT)
        if (treeNode.right != null) {
            visitTreeElement(treeNode.right!!, inserted.id!!, TreeElementPosition.RIGHT)
        }
    }

    private suspend fun FlowCollector<TreeElementEntity>.visitTreeLeaf(treeLeaf: TreeLeaf<String>, parentId: String?, position: TreeElementPosition?) {
        val metadata = treeLeaf.metadata as String
        LOGGER.debug("ecriture leaf {}", treeLeaf.value)
        val inserted = treeElementRepository.insert(
            TreeElementEntity(
                hash = treeLeaf.value,
                fileName = metadata,
                jobId = jobId,
                type = TreeElementType.LEAF,
                parentId = parentId,
                position = position
            )
        ).awaitSingle()
        //on renvoi le noeud cree à l'appeleur
        emit (inserted)

    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SaveRepositoryTreeVisitor::class.java)
    }

}