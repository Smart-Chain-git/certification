package com.sword.signature.business.service

import com.sword.signature.business.model.*
import kotlinx.coroutines.flow.Flow

interface SignService {


    fun batchSign(
        requester: Account,
        algorithm: Algorithm,
        flowName: String,
        fileHashs: Flow<Pair<String, FileMetadata>>
    ): Flow<Job>

    suspend fun getFileProof(requester: Account, fileId: String): Pair<Job, List<TreeElement>>?

}