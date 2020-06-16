package com.sword.signature.business.service

import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Proof

interface CheckService {

    @Throws(CheckException::class)
    suspend fun checkDocument(documentHash: String, providedProof: Proof? = null): CheckResponse
}