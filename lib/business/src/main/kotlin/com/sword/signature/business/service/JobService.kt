package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import kotlinx.coroutines.flow.Flow

interface JobService {

    fun findAllByUser(requester: Account, account: Account): Flow<Job>

}