package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.Job
import kotlinx.coroutines.flow.Flow

interface SignService {

    /**
     * Sign a batch of files.
     * @param requester The requester of the sign operation.
     * @param channelName The name of the channel used to sign.
     * @param algorithm The algorithm used to hash the files and to use to build the merkle tree.
     * @param flowName The name of the flow (only for identification).
     * @param callBackUrl The URL to call once the merkle tree root hash is on the blockchain and validated.
     * @param fileHashes The list of files to sign (first element: the hash, second element: the metadata).
     * @return The list of jobs created during the sign operation.
     */
    fun batchSign(
        requester: Account,
        channelName: String?,
        algorithm: Algorithm,
        flowName: String,
        callBackUrl: String? = null,
        customFields: Map<String, String>? = null,
        fileHashes: Flow<Pair<String, FileMetadata>>
    ): Flow<Job>
}