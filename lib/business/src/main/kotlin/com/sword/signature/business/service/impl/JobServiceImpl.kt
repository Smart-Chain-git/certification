package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.TreeElement
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JobService
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.model.mapper.toPredicate
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    private val treeElementRepository: TreeElementRepository

) : JobService {

    override fun findAllByUser(requester: Account, account: Account): Flow<Job> {
        if (!requester.isAdmin && requester.id != account.id) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to list ${account.login}'s jobs")
        }

        return jobRepository.findAllByUserId(account.id).map { it.toBusiness() }
    }

    override suspend fun findById(requester: Account, jobId: String, withLeaves: Boolean): Job? {
        val job = jobRepository.findById(jobId).awaitFirstOrNull() ?: return null
        LOGGER.debug("id : {}, found {}", jobId, job)
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        //recuperation des element de l'arbre de type feuille
        val treeElementPredicate = TreeElementCriteria(jobId = jobId, type = TreeElementType.LEAF).toPredicate()
        val leaves =
            if (withLeaves) {
                treeElementRepository.findAll(treeElementPredicate).asFlow()
                    .map { it.toBusiness() as TreeElement.LeafTreeElement }.toList()
            } else {
                null
            }
        LOGGER.debug("mes feuilles {}", leaves)

        return job.toBusiness(leaves)
    }

    override suspend fun patch(requester: Account, jobId: String, patch: JobPatch): Job {
        val job = jobRepository.findById(jobId).awaitFirstOrNull() ?: throw EntityNotFoundException("job", jobId)
        LOGGER.debug("id : {}, found {}", jobId, job)
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to update job: ${job.id}")
        }
        val toPatch = job.copy(
            numbreOfTry = patch.numbreOfTry ?: job.numbreOfTry,
            blockId = patch.blockId ?: job.blockId,
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
            }


        )

        val updatedJob = jobRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.trace("job with id ({}) updated.", jobId)
        return updatedJob
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JobServiceImpl::class.java)
    }
}