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
import com.sword.signature.tezos.reader.service.TezosReaderService
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
    @Value("\${sign.tree.maximumLeaf}") val maximumLeaf: Int,
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository,
    private val anchoringMessageChannel: MessageChannel
) : SignService {

    @Transactional(rollbackFor = [Exception::class])
    override fun batchSign(
        requester: Account,
        channelName: String?,
        algorithm: Algorithm,
        flowName: String,
        callBackUrl: String?,
        fileHashes: Flow<Pair<String, FileMetadata>>
    ): Flow<Job> {

        return flow {

            // intermediary doit etre declaré dans le scope du flow!
            val intermediary = mutableListOf<Pair<String, FileMetadata>>()
            fileHashes.collect { fileHash ->
                if (!algorithm.checkHashDigest(fileHash.first)) {
                    throw UserServiceException("bad ${algorithm.name} hash for file ${fileHash.second}")
                }
                intermediary.add(fileHash)
                if (intermediary.size >= maximumLeaf) {
                    emit(anchorTree(requester, channelName, algorithm, flowName, callBackUrl, intermediary))
                    intermediary.clear()
                }
            }
            // emission des derniers hash
            if (intermediary.isNotEmpty()) {
                emit(anchorTree(requester, channelName, algorithm, flowName, callBackUrl, intermediary))
            }
        }
    }

    private suspend fun anchorTree(
        requester: Account,
        channelName: String?,
        algorithm: Algorithm,
        flowName: String,
        callBackUrl: String?,
        fileHashes: List<Pair<String, FileMetadata>>
    ): Job {

        // Job creation.
        val jobEntity = jobRepository.insert(
            JobEntity(
                userId = requester.id,
                algorithm = algorithm.name,
                flowName = flowName,
                callBackUrl = callBackUrl,
                state = JobStateType.INSERTED,
                stateDate = OffsetDateTime.now(),
                docsNumber = fileHashes.size,
                channelName = channelName
            )
        ).awaitSingle()

        // Merkle tree creation.
        val merkleTree = TreeBuilder<String>(algorithm.name).elements(fileHashes).build()
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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SignServiceImpl::class.java)
    }
}