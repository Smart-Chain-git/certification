package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.FileHash
import com.sword.signature.business.model.Job
import kotlinx.coroutines.flow.Flow

interface SignService {


    fun batchSign(account: Account,fileHashs: Flow<FileHash>): Flow<Job>


}