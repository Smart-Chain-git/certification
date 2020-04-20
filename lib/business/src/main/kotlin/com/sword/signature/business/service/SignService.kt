package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.TreeElement
import kotlinx.coroutines.flow.Flow

interface SignService {


    fun batchSign(
        requester: Account,
        algorithm: Algorithm,
        flowName: String,
        fileHashs: Flow<Pair<String, String>>
    ): Flow<Job>

    suspend fun getFileProof(requester: Account, fileId: String): Pair<Job, List<TreeElement>>?

}