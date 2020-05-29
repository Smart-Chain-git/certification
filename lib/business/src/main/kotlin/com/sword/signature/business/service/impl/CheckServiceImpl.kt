package com.sword.signature.business.service.impl

import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Proof
import com.sword.signature.business.service.CheckService
import org.springframework.stereotype.Service

@Service
class CheckServiceImpl: CheckService {

    override fun checkDocument(documentHash: String, proof: Proof?) : CheckResponse {
        TODO("Not yet implemented")
    }
}