package com.sword.signature.business.service.impl

import com.sword.signature.business.model.*
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.FileService
import com.sword.signature.common.criteria.FileCriteria
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.mapper.toPredicate
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FileServiceImpl(
    private val treeElementRepository: TreeElementRepository,
    private val jobRepository: JobRepository,
    private val tezosReaderService: TezosReaderService,
    @Value("\${tezos.chain:#{null}}") private val tezosChain: String?,
    @Value("\${tezos.urls.api.storage:#{null}}") private val apiStorageUrl: String?,
    @Value("\${tezos.urls.api.transaction:#{null}}") private val apiTransactionUrl: String?,
    @Value("\${tezos.urls.web.provider:#{null}}") private val webProviderUrl: String?
) : FileService {
    override suspend fun getFileProof(requester: Account, fileId: String): Mono<Proof> {

        val leafElement = treeElementRepository.findById(fileId).awaitFirstOrNull() ?: return Mono.empty()
        val job = jobRepository.findById(leafElement.jobId).awaitSingle()

        // Check right to perform operation.
        if (!requester.isAdmin && requester.id != job.userId) {
            // TODO: replace later by MissingRightException once merged.
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        LOGGER.debug("Retrieving proof for file {}.", leafElement.metadata?.fileName)
        // Retrieve all siblings first.
        val elements = mutableListOf<Pair<String?, TreeElementPosition>>()
        var nextParent = leafElement.parentId?.let { treeElementRepository.findById(it).awaitFirst() }
        var element = leafElement
        while (nextParent != null) {
            val nextSiblingElement = findSibling(element.id!!, element.parentId)
            LOGGER.debug("Element {} added in siblings list.", nextSiblingElement?.id)
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

        // Add url nodes
        val urlNodes = mutableListOf<URLNode>()
        // API Storage Url
        if (apiStorageUrl != null && job.contractAddress != null) {
            getContractBigMapId(job.contractAddress!!).let {
                urlNodes.add(
                    URLNode.fromApiStorageUrl(
                        url = apiStorageUrl,
                        bigMapId = it.toString(),
                        rootHash = element.hash
                    )
                )
            }
        }
        // API Transaction Url
        if (apiTransactionUrl != null && job.transactionHash != null) {
            urlNodes.add(
                URLNode.fromApiTransactionUrl(
                    url = apiTransactionUrl,
                    transactionHash = job.transactionHash!!
                )
            )
        }
        // Web Provider Url
        webProviderUrl?.let {
            urlNodes.add(URLNode.fromWebProviderUrl(url = it))
        }

        val proof = Proof(
            signatureDate = job.injectedDate,
            filename = leafElement.metadata?.fileName,
            algorithm = job.algorithm,
            documentHash = leafElement.hash,
            rootHash = element.hash,
            hashes = elements,
            customFields = leafElement.metadata?.customFields,
            contractAddress = job.contractAddress,
            transactionHash = job.transactionHash,
            blockChain = tezosChain,
            blockHash = job.blockHash,
            signerAddress = job.signerAddress,
            creatorAddress = job.contractAddress?.let { getContractCreator(it) },
            urls =urlNodes
        )

        return Mono.just(proof)
    }

    override suspend fun getFiles(
        requester: Account,
        filter: FileFilter?,
        pageable: Pageable
    ): Flux<TreeElement.LeafTreeElement> {

        // Check right to perform operation.
        if (!requester.isAdmin && requester.id != filter?.accountId) {
            // TODO: replace later by MissingRightException once merged.
            throw IllegalAccessException("User ${requester.login} does not have to access this resource.")
        }

        val files = if (filter != null) {
            val jobCriteria: JobCriteria? =
                if (filter.accountId != null || filter.dateStart != null || filter.dateEnd != null)
                    JobCriteria(
                        accountId = filter.accountId,
                        dateStart = filter.dateStart,
                        dateEnd = filter.dateEnd
                    ) else null

            val allowedJobsIds: List<String>? =
                jobCriteria?.let { jobRepository.findAll(jobCriteria.toPredicate()).asFlow().map { it.id!! }.toList() }

            val documentCriteria = FileCriteria(
                id = filter.id,
                name = filter.name,
                hash = filter.hash,
                jobId = filter.jobId,
                jobIds = allowedJobsIds
            )

            treeElementRepository.findAll(documentCriteria.toPredicate(), pageable.sort)
        } else {
            treeElementRepository.findAll(FileCriteria().toPredicate(), pageable.sort)
        }

        return files.map { it.toBusiness() as TreeElement.LeafTreeElement }
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

    // Local index of contracts
    private val contracts = mutableMapOf<String, TzContract>()

    private suspend fun getContractCreator(contractAddress: String): String? {
        if (contracts.containsKey(contractAddress)) {
            return contracts[contractAddress]!!.manager
        } else {
            try {
                val contract = tezosReaderService.getContract(contractAddress)
                contract?.let {
                    contracts[contractAddress] = it
                    return it.manager
                }
            } catch (e: Exception) {
                LOGGER.error("Indexer can't be used to retrieve contract creator.")
            }
            return null
        }
    }

    private suspend fun getContractBigMapId(contractAddress: String): Long? {
        if (contracts.containsKey(contractAddress) && contracts[contractAddress]!!.bigMapIds.isNotEmpty()) {
            return contracts[contractAddress]!!.bigMapIds[0]
        } else {
            try {
                val contract = tezosReaderService.getContract(contractAddress)
                contract?.let {
                    contracts[contractAddress] = it
                    if (it.bigMapIds.isNotEmpty()) {
                        return it.bigMapIds[0]
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Indexer can't be used to retrieve contract creator.")
            }
            return null
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FileServiceImpl::class.java)
    }
}