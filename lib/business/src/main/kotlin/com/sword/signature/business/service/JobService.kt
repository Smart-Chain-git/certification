package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import com.sword.signature.common.criteria.JobCriteria
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface JobService {

    fun findAll(requester: Account, criteria: JobCriteria? = null, pageable: Pageable = Pageable.unpaged()): Flow<Job>

    suspend fun findById(requester: Account, jobId: String, withLeaves: Boolean = false): Job?

    suspend fun patch(requester: Account, jobId: String, patch: JobPatch): Job


}