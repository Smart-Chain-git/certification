package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.*
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.SignService
import com.sword.signature.business.visitor.SaveRepositoryTreeVisitor
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.merkletree.builder.TreeBuilder
import com.sword.signature.merkletree.visitor.SimpleAlgorithmTreeBrowser
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.mapper.toPredicate
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class SignServiceImpl(
    @Value("\${sign.tree.maximunLeaf}") val maximunLeaf: Int,
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository,
    private val anchoringMessageChannel: MessageChannel
) : SignService {

    @Transactional(rollbackFor = [Exception::class])
    override fun batchSign(
        requester: Account,
        algorithm: Algorithm,
        flowName: String,
        callBackUrl: String?,
        fileHashs: Flow<Pair<String, FileMetadata>>
    ): Flow<Job> {

        return flow {

            // intermediary doit etre declaré dans le scope du flow!
            val intermediary = mutableListOf<Pair<String, FileMetadata>>()
            fileHashs.collect { fileHash ->
                if (!algorithm.checkHashDigest(fileHash.first)) {
                    throw UserServiceException("bad ${algorithm.name} hash for file ${fileHash.second}")
                }
                intermediary.add(fileHash)
                if (intermediary.size >= maximunLeaf) {
                    emit(anchorTree(requester, algorithm, flowName, callBackUrl, intermediary))
                    intermediary.clear()
                }
            }
            // emission des derniers hash
            if (intermediary.isNotEmpty()) {
                emit(anchorTree(requester, algorithm, flowName, callBackUrl, intermediary))
            }
        }
    }

    private suspend fun anchorTree(
        requester: Account,
        algorithm: Algorithm,
        flowName: String,
        callBackUrl: String?,
        fileHashs: List<Pair<String, FileMetadata>>
    ): Job {

        // Job creation.
        val jobEntity = jobRepository.insert(
            JobEntity(
                userId = requester.id,
                algorithm = algorithm.name,
                flowName = flowName,
                callBackUrl = callBackUrl,
                state = JobStateType.INSERTED,
                stateDate = OffsetDateTime.now()
            )
        ).awaitSingle()

        // Merkle tree creation.
        val merkleTree = TreeBuilder<String>(algorithm.name).elements(fileHashs).build()
        // Compute all merkle tree nodes value.
        SimpleAlgorithmTreeBrowser(algorithm.name).visitTree(merkleTree)

        // Save the merkle tree.
        val inserted = SaveRepositoryTreeVisitor(
            jobId = jobEntity.id!!,
            treeElementRepository = treeElementRepository
        ).visitTree(merkleTree)

        // Send a message to the anchoring channel to trigger the daemon anchoring job
        anchoringMessageChannel.send(
            MessageBuilder.withPayload(
                AnchorJobMessagePayload(
                    jobId = jobEntity.id!!
                )
            ).build()
        )

        return jobEntity.toBusiness(files = inserted.filter { it.type == TreeElementType.LEAF }
            .map { it.toBusiness() as TreeElement.LeafTreeElement }.toList())
    }

    @Transactional
    override suspend fun getFileProof(requester: Account, fileId: String): Proof? {
        val leafElement = treeElementRepository.findById(fileId).awaitFirstOrNull() ?: return null
        val job = jobRepository.findById(leafElement.jobId).awaitSingle()
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        LOGGER.debug("Retrieving proof for document {}.", leafElement.metadata?.fileName)

        val elements = mutableListOf<Pair<String?, TreeElementPosition>>()
        var nextParent = leafElement.parentId?.let { treeElementRepository.findById(it).awaitFirst() }
        var element = leafElement
        while (nextParent != null) {
            val nextSiblingElement = findSibling(element.id!!, element.parentId)
            LOGGER.debug("Element ({}) added in siblings list.", nextSiblingElement?.id)
            elements.add(
                if (nextSiblingElement == null) {
                    Pair(
                        null, if (element.position == TreeElementPosition.RIGHT) {
                            TreeElementPosition.LEFT
                        } else {
                            TreeElementPosition.RIGHT
                        }
                    )
                } else {
                    Pair(
                        nextSiblingElement.hash,
                        nextSiblingElement.position!! // Position is not nullable in a tree element.
                    )
                }
            )
            element = nextParent
            nextParent = element.parentId?.let { treeElementRepository.findById(it).awaitFirst() }
        }

        return Proof(
            signatureDate = job.injectedDate,
            filename = leafElement.metadata?.fileName,
            algorithm = job.algorithm,
            documentHash = leafElement.hash,
            rootHash = element.hash,
            hashes = elements,
            customFields = leafElement.metadata?.customFields,
            contractAddress = job.contractAddress,
            transactionHash = job.transactionHash,
            blockHash = job.blockHash
        )
    }

    private suspend fun findSibling(id: String, parentId: String?): TreeElementEntity? {
        if (parentId == null) {
            return null
        }
        val treeElementPredicate = TreeElementCriteria(notId = id, parentId = parentId).toPredicate()
        return treeElementRepository.findOne(
            treeElementPredicate
        ).awaitFirstOrNull()

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SignServiceImpl::class.java)
    }

}