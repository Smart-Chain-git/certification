package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.*
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.SignService
import com.sword.signature.business.visitor.SaveRepositoryTreeVisitor
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.merkletree.builder.TreeBuilder
import com.sword.signature.merkletree.visitor.SimpleAlgorithmTreeBrowser
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
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
    private val jobToAnchorsMessageChannel: MessageChannel
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

        // creation du jobb en BDD
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

        //creation de l'arbre
        val merkleTree = TreeBuilder<String>(algorithm.name).elements(fileHashs).build()
        //calcul du hash des noeud
        SimpleAlgorithmTreeBrowser(algorithm.name).visitTree(merkleTree)
        // ecriture en BDD de l'arbre

        val inserted = SaveRepositoryTreeVisitor(
            jobId = jobEntity.id!!,
            treeElementRepository = treeElementRepository
        ).visitTree(merkleTree)

        //inscription d'un message pour le daemon qu'il sache qu'il doit encrer la transaction
        jobToAnchorsMessageChannel.send(
            MessageBuilder.withPayload(
                AnchorJobMessagePayload(
                    jobEntity.id!!
                )
            ).build()
        )

        return jobEntity.toBusiness(files = inserted.filter { it.type == TreeElementType.LEAF }
            .map { it.toBusiness() as TreeElement.LeafTreeElement }.toList())
    }

    @Transactional
    override suspend fun getFileProof(requester: Account, fileId: String): Pair<Job, List<TreeElement>>? {
        val treeElement = treeElementRepository.findById(fileId).awaitFirstOrNull() ?: return null
        val job = jobRepository.findById(treeElement.jobId).awaitSingle()
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        val elements = mutableListOf(treeElement)
        var nextId = treeElement.parentId
        while (nextId != null) {
            val nextElement = treeElementRepository.findById(nextId).awaitSingle()
            elements.add(nextElement)
            nextId = nextElement.parentId
        }

        return Pair(job.toBusiness(), elements.map { it.toBusiness() })
    }
}