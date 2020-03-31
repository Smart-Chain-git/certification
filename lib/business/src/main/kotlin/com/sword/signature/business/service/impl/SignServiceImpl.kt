package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.FileHash
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.SignService
import com.sword.signature.business.visitor.SaveRepositoryTreeVisitor
import com.sword.signature.merkletree.builder.TreeBuilder
import com.sword.signature.merkletree.visitor.SHA256TreeVisitor
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.entity.TreeElementType
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SignServiceImpl(
    @Value("\${sign.tree.maximunLeaf}") val maximunLeaf: Int,
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository
) : SignService {

    @Transactional
    override fun batchSign(account: Account, fileHashs: Flow<FileHash>): Flow<Job> {

        return flow {

            val intermediary = mutableListOf<FileHash>()
            fileHashs.collect { fileHash ->
                intermediary.add(fileHash)
                if (intermediary.size >= maximunLeaf) {
                    emit(anchorTree(account, "SHA-256", intermediary))
                    intermediary.clear()
                }
            }
            // emission des derniers hash
            if (intermediary.isNotEmpty()) {
                emit(anchorTree(account, "SHA-256", intermediary))
            }
        }
    }

    private suspend fun anchorTree(account: Account, algorithm: String, fileHashs: List<FileHash>): Job {

        // creation du jobb en BDD
        val jobEntity = jobRepository.save(
            JobEntity(
                userId = account.id,
                algorithm = algorithm
            )
        ).awaitFirst()

        //creation de l'arbre
        val elements = fileHashs.map {
            Pair(
                it.hash, it
            )
        }


        val merkleTree = TreeBuilder<String>(algorithm).elements(elements).build()
        //calcul du hash des noeud
        SHA256TreeVisitor().visitTree(merkleTree)
        // ecriture en BDD de l'arbre

        SaveRepositoryTreeVisitor(
            jobId = jobEntity.id!!,
            treeElementRepository = treeElementRepository
        ).visitTree(merkleTree)


        return jobEntity.toBusiness(files = fileHashs.map { it.fileName })


    }


    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SignServiceImpl::class.java)
    }

}