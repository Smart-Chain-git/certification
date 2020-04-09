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

    override fun findAllByUser(account: Account): Flow<Job> {
        return jobRepository.findAllByUserId(account.id).map { it.toBusiness() }
    }
}