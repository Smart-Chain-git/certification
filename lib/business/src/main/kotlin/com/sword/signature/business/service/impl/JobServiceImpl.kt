package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.TreeElement
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JobService
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.model.entity.QTreeElementEntity
import com.sword.signature.model.mapper.toPredicate
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository

) : JobService {

    override fun findAll(requester: Account, criteria: JobCriteria?, pageable: Pageable): Flow<Job> {
        if (!requester.isAdmin && requester.id != criteria?.accountId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to list jobs for accountId=[${criteria?.accountId}]")
        }

        return if (criteria == null) {
            jobRepository.findAll(pageable.sort)
        } else {
            jobRepository.findAll(criteria.toPredicate(), pageable.sort)
        }.asFlow()
        .paginate(pageable)
        .map { it.toBusiness() }

    }

    override fun countAll(requester: Account, criteria: JobCriteria?): Long {
        if (!requester.isAdmin && requester.id != criteria?.accountId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to list jobs for accountId=[${criteria?.accountId}]")
        }

        return if (criteria == null) {
            jobRepository.count()
        } else {
            jobRepository.count(criteria.toPredicate())
        }.block()!!
    }

    override suspend fun findById(requester: Account, jobId: String, withLeaves: Boolean): Job? {
        val job = jobRepository.findById(jobId).awaitFirstOrNull() ?: return null
        LOGGER.debug("id : {}, found {}", jobId, job)
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        // Retrieve the merkle tree root hash.
        val rootElementPredicate =
            QTreeElementEntity.treeElementEntity.parentId.isNull.and(QTreeElementEntity.treeElementEntity.jobId.eq(jobId))
        val rootHash = treeElementRepository.findOne(rootElementPredicate).awaitFirstOrNull()?.hash

        // Retrieve the leaves if needed.
        val treeElementPredicate = TreeElementCriteria(jobId = jobId, type = TreeElementType.LEAF).toPredicate()
        val leaves =
            if (withLeaves) {
                treeElementRepository.findAll(treeElementPredicate).asFlow()
                    .map { it.toBusiness(job.toBusiness()) as TreeElement.LeafTreeElement }.toList()
            } else {
                null
            }
        LOGGER.debug("Job {}: leaves retrieved {}.", jobId, leaves)

        return job.toBusiness(rootHash, leaves)
    }

    override suspend fun patch(requester: Account, jobId: String, patch: JobPatch): Job {
        val job = jobRepository.findById(jobId).awaitFirstOrNull() ?: throw EntityNotFoundException("job", jobId)
        LOGGER.debug("id : {}, found {}", jobId, job)
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to update job: ${job.id}")
        }
        val toPatch = job.copy(
            numberOfTry = patch.numberOfTry ?: job.numberOfTry,
            transactionHash = patch.transactionHash ?: job.transactionHash,
            timestamp = patch.timestamp ?: job.timestamp,
            blockHash = patch.blockHash ?: job.blockHash,
            blockDepth = patch.blockDepth ?: job.blockDepth,
            state = patch.state ?: job.state,
            injectedDate = if (patch.state == JobStateType.INJECTED) {
                OffsetDateTime.now()
            } else {
                job.injectedDate
            },
            validatedDate = if (patch.state == JobStateType.VALIDATED) {
                OffsetDateTime.now()
            } else {
                job.validatedDate
            },
            stateDate = if (patch.state != null) {
                OffsetDateTime.now()
            } else {
                job.stateDate
            },
            contractAddress = patch.contractAddress ?: job.contractAddress,
            signerAddress = patch.signerAddress ?: job.signerAddress
        )

        val updatedJob = jobRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.trace("job with id ({}) updated.", jobId)
        return updatedJob
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JobServiceImpl::class.java)
    }
}
