package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.SignService
import com.sword.signature.business.visitor.SaveRepositoryTreeVisitor
import com.sword.signature.merkletree.builder.TreeBuilder
import com.sword.signature.merkletree.visitor.SimpleAlgorithmTreeBrowser
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignServiceImpl(
    @Value("\${sign.tree.maximunLeaf}") val maximunLeaf: Int,
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository
) : SignService {

    @Transactional
    override fun batchSign(account: Account, algorithm: Algorithm, fileHashs: Flow<Pair<String, String>>): Flow<Job> {

        return flow {

            val intermediary = mutableListOf<Pair<String, String>>()
            fileHashs.collect { fileHash ->
                intermediary.add(fileHash)
                if (intermediary.size >= maximunLeaf) {
                    emit(anchorTree(account, algorithm, intermediary))
                    intermediary.clear()
                }
            }
            // emission des derniers hash
            if (intermediary.isNotEmpty()) {
                emit(anchorTree(account, algorithm, intermediary))
            }
        }
    }

    private suspend fun anchorTree(account: Account, algorithm: Algorithm, fileHashs: List<Pair<String, String>>): Job {

        // creation du jobb en BDD
        val jobEntity = jobRepository.save(
            JobEntity(
                userId = account.id,
                algorithm = algorithm.name
            )
        ).awaitSingle()

        //creation de l'arbre
        val merkleTree = TreeBuilder<String>(algorithm.name).elements(fileHashs).build()
        //calcul du hash des noeud
        SimpleAlgorithmTreeBrowser(algorithm.name).visitTree(merkleTree)
        // ecriture en BDD de l'arbre

        SaveRepositoryTreeVisitor(
            jobId = jobEntity.id!!,
            treeElementRepository = treeElementRepository
        ).visitTree(merkleTree)


        return jobEntity.toBusiness(files = fileHashs.map { it.second })


    }
}