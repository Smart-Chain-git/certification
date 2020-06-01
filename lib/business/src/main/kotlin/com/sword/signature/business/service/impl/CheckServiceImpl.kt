package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Proof
import com.sword.signature.business.service.CheckService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.entity.QTreeElementEntity
import com.sword.signature.model.entity.TreeElementEntity
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CheckServiceImpl(
    val treeElementRepository: TreeElementRepository,
    val jobRepository: JobRepository,
    val tezosReaderService: TezosReaderService
) : CheckService {

    override suspend fun checkDocument(documentHash: String, proof: Proof?): CheckResponse {
        LOGGER.debug("Checking document for hash: {}", documentHash)

        if (proof == null) { // If only the document hash is provided.
            val treeElement: TreeElementEntity =
                treeElementRepository.findOne(QTreeElementEntity.treeElementEntity.hash.eq(documentHash))
                    .awaitFirstOrNull()
                    ?: throw CheckException.HashNotPresent(documentHash)
            val job: JobEntity =
                jobRepository.findById(treeElement.jobId).awaitFirstOrNull() ?: throw CheckException.IncoherentData()
            // Check the tree and retrieve the root hash.
            val rootHash = checkExistingTree(job.algorithm, treeElement)
            when (job.state) {
                JobStateType.INSERTED -> throw CheckException.DocumentKnownUnknownRootHash(
                    signer = "signer",
                    publicKey = "public_key",
                    date = job.stateDate
                )
                JobStateType.INJECTED -> throw CheckException.TransactionNotDeepEnough(
                    currentDepth = job.blockDepth!!,
                    expectedDepth = job.minDepth!!
                )
                JobStateType.VALIDATED -> {
                    if (job.transactionHash != null) {
                        throw CheckException.IncoherentData()
                    }
                    val transaction: TzOp? = tezosReaderService.getTransaction(job.transactionHash!!)
                    val depth: Long? = tezosReaderService.getTransactionDepth(job.transactionHash!!)

                    if (transaction == null) {
                        LOGGER.error("No transaction found with hash '{}'", job.transactionHash!!)
                        throw CheckException.IncoherentData()
                    }
                    if (transaction.bigMapDiff.meta.contract != job.contractAddress) {
                        LOGGER.error(
                            "Different contract found for transaction '{}': expected '{}', actual '{}'.",
                            job.transactionHash!!,
                            job.contractAddress,
                            transaction.bigMapDiff.meta.contract
                        )
                        throw CheckException.IncoherentData()
                    }

                    // TODO
                }
            }
        } else {
            // TODO
        }
        return CheckResponse("In Progress")
    }

    /**
     * Check existing merkle tree coherence by computing all hashes in the branch and comparing it to the stored value.
     */
    private suspend fun checkExistingTree(algorithmName: String, leaf: TreeElementEntity): String {
        var current: TreeElementEntity = leaf
        while (current.parentId != null) {
            // Retrieve sibling.
            val sibling: TreeElementEntity? =
                treeElementRepository.findOne(
                    QTreeElementEntity.treeElementEntity.id.ne(current.id)
                        .and(QTreeElementEntity.treeElementEntity.parentId.eq(current.id))
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
        }
        // Return the merkle tree hash.
        LOGGER.debug("Merkle tree branch validated from leaf '{}' to root '{}'.", leaf.hash, current.hash)
        return current.hash
    }

    private fun checkJob() {}

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CheckServiceImpl::class.java)
    }
}