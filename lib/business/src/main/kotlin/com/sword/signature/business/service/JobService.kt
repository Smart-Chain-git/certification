package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import kotlinx.coroutines.flow.Flow

interface JobService {

    fun findAllByUser(requester: Account, account: Account): Flow<Job>


    suspend fun findById(requester: Account, jobId: String, withLeaves: Boolean = false): Job?

    suspend fun patch(requester: Account, jobId: String, patch: JobPatch): Job

}