package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.JobService
import com.sword.signature.model.repository.JobRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository
) : JobService {

    override fun findAllByUser(requester: Account, account: Account): Flow<Job> {
        if(!requester.isAdmin && requester.id!=account.id) {
            throw IllegalAccessException("user ${requester.login} dont have Role to list ${account.login}'s jobs")
        }

        return jobRepository.findAllByUserId(account.id).map { it.toBusiness() }
    }
}