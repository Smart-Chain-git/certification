package com.sword.signature.business.service

import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Proof

interface CheckService {

    fun checkDocument(documentHash: String, proof: Proof?): CheckResponse
}