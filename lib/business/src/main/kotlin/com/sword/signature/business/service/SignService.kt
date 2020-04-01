package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import kotlinx.coroutines.flow.Flow

interface SignService {


    fun batchSign(account: Account, algorithm: String, fileHashs: Flow<Pair<String, String>>): Flow<Job>


}