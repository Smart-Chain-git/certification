package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JobService
import com.sword.signature.common.criteria.TreeElementCriteria
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.model.mapper.toPredicate
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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

    override suspend fun findById(requester: Account, jobId: String): Job? {
        val job = jobRepository.findById(jobId).awaitFirstOrNull() ?: return null
        LOGGER.debug("id : {}, found {}", jobId, job)
        if (!requester.isAdmin && requester.id != job.userId) {
            throw IllegalAccessException("user ${requester.login} does not have role/permission to get job: ${job.id}")
        }

        //recuperation des element de l'arbre de type feuille
        val treeElementPredicate = TreeElementCriteria(jobId = jobId, type = TreeElementType.LEAF).toPredicate()
        val leaves = treeElementRepository.findAll(treeElementPredicate).asFlow().map { it.fileName!! }.toList()

        LOGGER.debug("mes feuilles {}", leaves)

        return job.toBusiness(leaves)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JobServiceImpl::class.java)
    }
}