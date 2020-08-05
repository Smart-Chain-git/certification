package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.Proof
import com.sword.signature.business.service.CheckService
import com.sword.signature.business.service.FileService
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.entity.QTreeElementEntity
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.repository.AccountRepository
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.function.Supplier

@Service
class CheckServiceImpl(
    val treeElementRepository: TreeElementRepository,
    val jobRepository: JobRepository,
    val accountRepository: AccountRepository,
    val tezosReaderService: TezosReaderService,
    val fileService: FileService,
    val jobService: JobService,
    @Value("\${tezos.validation.minDepth}") private val minDepth: Long
) : CheckService {

    private val adminAccount =
        Account(
            id = "", login = "", email = "", password = "", fullName = "checkService",
            company = "", country = "", publicKey = "", hash = "", isAdmin = true, disabled = false, firstLogin = false
        )

    override suspend fun checkDocument(documentHash: String, providedProof: Proof?): CheckResponse {
        LOGGER.debug("Checking document for hash: {}", documentHash)

        if (providedProof == null) { // If only the document hash is provided.
            val documentsAndJobs = getDocumentsAndJobsFromHash(documentHash)
            treeElementRepository.findAll(QTreeElementEntity.treeElementEntity.hash.eq(documentHash)).asFlow()
                .toList()
            if (documentsAndJobs.isEmpty()) {
                throw CheckException.HashNotPresent(documentHash)
            }
            val treeElement = documentsAndJobs[0].first
            val job = documentsAndJobs[0].second
            // Check the tree and retrieve the root hash.
            val branchHashes = checkExistingTree(job.algorithm, treeElement)
            val rootHash: String = if (branchHashes.isNotEmpty()) branchHashes[branchHashes.size - 1] else documentHash
            when (job.state) {
                JobStateType.REJECTED -> throw CheckException.HashNotPresent(documentHash = documentHash)
                JobStateType.INSERTED -> {
                    // Retrieve the signer.
                    val signer: AccountEntity? = accountRepository.findById(job.userId).awaitFirstOrNull()
                    throw CheckException.DocumentKnownUnknownRootHash(
                        signer = signer?.fullName,
                        publicKey = job.signerAddress,
                        date = job.stateDate
                    )
                }
                JobStateType.INJECTED -> throw CheckException.TransactionNotDeepEnough(
                    currentDepth = job.blockDepth ?: 0,
                    expectedDepth = minDepth
                )
                JobStateType.VALIDATED -> {
                    if (job.transactionHash == null) {
                        throw CheckException.IncoherentData()
                    }
                    val transaction: TzOp? = tezosReaderService.getTransaction(job.transactionHash)
                    val depth: Long? = tezosReaderService.getTransactionDepth(job.transactionHash)

                    if (transaction == null) {
                        LOGGER.error("No transaction found with hash '{}'", job.transactionHash)
                        throw CheckException.IncoherentData()
                    }
                    if (transaction.bigMapDiff[0].meta.contract != job.contractAddress) {
                        LOGGER.error(
                            "Different contract found for transaction '{}': expected '{}', actual '{}'.",
                            job.transactionHash,
                            job.contractAddress,
                            transaction.bigMapDiff[0].meta.contract
                        )
                        throw CheckException.IncoherentData()
                    }

                    if (transaction.bigMapDiff[0].key != rootHash) {
                        LOGGER.error(
                            "Different rootHash found for transaction '{}': expected '{}', actual '{}'.",
                            job.transactionHash,
                            rootHash,
                            transaction.bigMapDiff[0].key
                        )
                        throw CheckException.IncoherentData()
                    }

                    if (transaction.bigMapDiff[0].value.address != job.signerAddress) {
                        LOGGER.error(
                            "Different signer found for transaction '{}': expected '{}', actual '{}'.",
                            job.transactionHash,
                            job.signerAddress,
                            transaction.bigMapDiff[0].value.address
                        )
                        throw CheckException.IncoherentData()
                    }

                    if (depth == null || depth < minDepth) {
                        LOGGER.error(
                            "Depth is undefined or not deep enough for transaction '{}': expected '{}', actual '{}'",
                            job.transactionHash,
                            minDepth,
                            depth
                        )
                        throw CheckException.IncoherentData()
                    }

                    // Compute the file proof.
                    val computedProof = fileService.getFileProof(adminAccount, treeElement.id!!).awaitFirstOrNull()
                        ?: throw CheckException.IncoherentData()
                    // Retrieve the signer.
                    val signer: AccountEntity? = accountRepository.findById(job.userId).awaitFirstOrNull()

                    return CheckResponse(
                        status = 1,
                        fileId = treeElement.id!!,
                        jobId = job.id,
                        signer = signer?.fullName,
                        timestamp = transaction.bigMapDiff[0].value.timestamp,
                        trace = branchHashes,
                        proof = computedProof
                    )
                }
                else -> throw CheckException.IncoherentData()
            }

        } else {
            // Check the proof file
            try {
                checkNotNull(providedProof.transactionHash)
            } catch (e: Exception) {
                LOGGER.debug("Proof for {} does not contain every required fields.", documentHash)
                throw CheckException.IncorrectProofFile()
            }

            if (documentHash != providedProof.documentHash) {
                LOGGER.error(
                    "Provided hash ({}) is not compliant with proof hash ({}).",
                    documentHash,
                    providedProof.documentHash
                )
                throw CheckException.HashInconsistent(
                    documentHash = documentHash,
                    proofDocumentHash = providedProof.documentHash
                )
            }

            // Check the proof compliance
            val branchHashes = checkProofTree(providedProof)

            // Retrieve the transaction and depth
            val transaction: TzOp? = tezosReaderService.getTransaction(providedProof.transactionHash)
            val depth: Long? = tezosReaderService.getTransactionDepth(providedProof.transactionHash)
            val contract: TzContract? = providedProof.contractAddress?.let { tezosReaderService.getContract(it) }

            if (transaction == null) {
                LOGGER.error("Provided transaction '{}' not found in the blockchain.", providedProof.transactionHash)
                throw CheckException.NoTransaction(rootHash = providedProof.rootHash)
            }

            if (transaction.bigMapDiff[0].meta.contract != providedProof.contractAddress) {
                LOGGER.error(
                    "Different contract found for transaction '{}': expected '{}', actual '{}'.",
                    providedProof.transactionHash,
                    providedProof.contractAddress,
                    transaction.bigMapDiff[0].meta.contract
                )
                throw CheckException.IncorrectTransaction()
            }

            if (contract?.manager != providedProof.creatorAddress) {
                LOGGER.error(
                    "Different contract manager found for transaction '{}': expected '{}', actual '{}'.",
                    providedProof.transactionHash,
                    contract?.manager,
                    providedProof.creatorAddress
                )
                throw CheckException.IncoherentOriginPublicKey(
                    originPublicKey = contract?.manager ?: "undefined",
                    proofOriginPublicKey = providedProof.creatorAddress ?: "undefined"
                )
            }

            if (transaction.bigMapDiff[0].key != providedProof.rootHash) {
                LOGGER.error(
                    "Different rootHash found for transaction '{}': expected '{}', actual '{}'.",
                    providedProof.transactionHash,
                    providedProof.rootHash,
                    transaction.bigMapDiff[0].key
                )
                throw CheckException.IncorrectTransaction()
            }

            if (transaction.bigMapDiff[0].value.address != providedProof.signerAddress) {
                LOGGER.error(
                    "Different signer found for transaction '{}': expected '{}', actual '{}'.",
                    providedProof.transactionHash,
                    providedProof.signerAddress,
                    transaction.bigMapDiff[0].value.address
                )
                throw CheckException.IncorrectTransaction()
            }

            if (depth == null || depth < minDepth) {
                LOGGER.error(
                    "Depth is undefined or not deep enough for transaction '{}': expected '{}', actual '{}'",
                    providedProof.transactionHash,
                    30,
                    depth
                )
                throw CheckException.TransactionNotDeepEnough(
                    currentDepth = depth ?: 0,
                    expectedDepth = minDepth
                )
            }

            val defaultResponse = Supplier {
                CheckResponse(
                    status = 2,
                    timestamp = transaction.bigMapDiff[0].value.timestamp,
                    trace = branchHashes,
                    proof = providedProof
                )
            }

            // Check the database
            val documentsAndJob =
                getDocumentsAndJobsFromHash(documentHash).filter { it.second.rootHash == providedProof.rootHash }
            if (documentsAndJob.isEmpty()) {
                return defaultResponse.get()
            }
            val treeElement = documentsAndJob[0].first
            val job = documentsAndJob[0].second

            // Check compliance
            if (providedProof.transactionHash != job.transactionHash || !checkBranch(providedProof, treeElement)) {
                return defaultResponse.get()
            }
            // Generate a fresh proof
            val freshProof: Proof =
                fileService.getFileProof(adminAccount, treeElement.id!!).awaitFirstOrNull()
                    ?: return defaultResponse.get()
            // Retrieve the signer.
            val signer: AccountEntity? = accountRepository.findById(job.userId).awaitFirstOrNull()

            return CheckResponse(
                status = 1,
                fileId = treeElement.id!!,
                jobId = job.id,
                signer = signer?.fullName,
                timestamp = OffsetDateTime.now(),
                trace = branchHashes,
                proof = freshProof
            )
        }
    }

    /**
     * Retrieve all document/job pairs for the given document hash.
     * @param documentHash Hash of document to search.
     * @return List of documents/job pairs.
     */
    private suspend fun getDocumentsAndJobsFromHash(documentHash: String): List<Pair<TreeElementEntity, Job>> {
        val treeElements =
            treeElementRepository.findAll(QTreeElementEntity.treeElementEntity.hash.eq(documentHash)).asFlow().toList()
        val documentsAndJobs = mutableListOf<Pair<TreeElementEntity, Job>>()
        for (treeElement in treeElements) {
            val job = jobService.findById(requester = adminAccount, jobId = treeElement.jobId)
            job?.let { documentsAndJobs.add(Pair(treeElement, it)) }
        }
        return documentsAndJobs.sortedByDescending { p -> p.second.state }
    }

    /**
     * Check existing merkle tree coherence by computing all hashes in the branch and comparing it to the stored value.
     */
    private suspend fun checkExistingTree(algorithmName: String, leaf: TreeElementEntity): List<String> {
        val branchHashes = mutableListOf<String>()
        var current: TreeElementEntity = leaf
        while (current.parentId != null) {
            // Retrieve sibling.
            val sibling: TreeElementEntity? =
                treeElementRepository.findOne(
                    QTreeElementEntity.treeElementEntity.id.ne(current.id)
                        .and(QTreeElementEntity.treeElementEntity.parentId.eq(current.parentId))
                ).awaitFirstOrNull()
            // Retrieve parent.
            val parent: TreeElementEntity =
                treeElementRepository.findById(current.parentId!!).awaitFirstOrNull()
                    ?: throw CheckException.IncoherentData()
            // Compute the hash.
            val calculatedHash: String
            when (current.position) {
                TreeElementPosition.LEFT -> {
                    calculatedHash = if (sibling != null) {
                        hexStringHash(algorithmName, current.hash + sibling.hash)
                    } else {
                        hexStringHash(algorithmName, current.hash)
                    }
                }
                TreeElementPosition.RIGHT -> {
                    if (sibling == null) {
                        throw CheckException.IncoherentData()
                    }
                    calculatedHash = hexStringHash(algorithmName, sibling.hash + current.hash)
                }
                null -> throw CheckException.IncoherentData()
            }
            // Compare stored and computed hash.
            if (calculatedHash != parent.hash) {
                throw CheckException.IncoherentData()
            }
            current = parent
            branchHashes.add(current.hash)
        }
        // Return the merkle tree hash.
        LOGGER.debug("Merkle tree branch validated from leaf '{}' to root '{}'.", leaf.hash, current.hash)
        return branchHashes
    }

    private fun checkProofTree(proof: Proof): List<String> {
        val branchHashes = mutableListOf<String>()

        val algorithmName = proof.algorithm
        var currentHash = proof.documentHash

        for (sibling in proof.hashes) {
            val siblingHash = sibling.first
            val position = sibling.second

            currentHash = when (position) {
                TreeElementPosition.LEFT -> if (siblingHash == null) {
                    LOGGER.error("A left sibling cannot be a empty node.")
                    throw CheckException.IncorrectProofFile()
                } else {
                    hexStringHash(algorithmName, siblingHash + currentHash)
                }
                TreeElementPosition.RIGHT -> if (siblingHash == null) {
                    hexStringHash(algorithmName, currentHash)
                } else {
                    hexStringHash(algorithmName, currentHash + siblingHash)
                }
            }
            branchHashes.add(currentHash)
        }

        if (currentHash != proof.rootHash) {
            throw CheckException.IncorrectRootHash(documentHash = currentHash, proofDocumentHash = proof.rootHash)
        }

        return branchHashes
    }

    private suspend fun checkBranch(proof: Proof, treeElement: TreeElementEntity): Boolean {
        var current = treeElement
        var index = 0

        while (current.parentId != null) {
            if (index >= proof.hashes.size) {
                return false
            }
            val hash = proof.hashes[index++]
            val sibling: TreeElementEntity? =
                treeElementRepository.findOne(
                    QTreeElementEntity.treeElementEntity.id.ne(current.id)
                        .and(QTreeElementEntity.treeElementEntity.parentId.eq(current.parentId))
                ).awaitFirstOrNull()
            // Retrieve parent.
            val parent: TreeElementEntity =
                treeElementRepository.findById(current.parentId!!).awaitFirstOrNull()
                    ?: return false
            if (hash.first != sibling?.hash || hash.second != (sibling?.position ?: TreeElementPosition.RIGHT)) {
                return false
            }
            current = parent
        }

        if (index != proof.hashes.size) {
            return false
        }

        return true
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CheckServiceImpl::class.java)
    }
}
