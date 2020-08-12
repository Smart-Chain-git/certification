package com.sword.signature.business.service

import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.CheckResponse
import com.sword.signature.business.model.Proof

interface CheckService {

    /**
     * Check a document which hash is provided and proof (optional).
     * @param documentHash Hash of the document to check.
     * @param providedProof Proof of the document (optional).
     * @throws CheckException if the check is KO.
     * @return CheckResponse if the check is OK.
     */
    @Throws(CheckException::class)
    suspend fun checkDocument(documentHash: String, providedProof: Proof? = null): CheckResponse
}
