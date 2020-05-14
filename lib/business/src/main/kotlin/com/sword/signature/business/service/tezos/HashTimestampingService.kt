package com.sword.signature.business.service.tezos

import org.ej4tezos.impl.TezosAbstractService
import org.slf4j.LoggerFactory

class HashTimestampingService : TezosAbstractService() {

    private val hashRunOperationTemplate = loadTemplate("operations/hash_runOperation.json")
    private val hashForgeOperationTemplate = loadTemplate("operations/hash_forgeOperation.json")
    private val hashPreApplyOperationTemplate = loadTemplate("operations/hash_preApplyOperation.json")


    fun timestampHash(rootHash: String): String {
        LOGGER.debug("timestampHash (rootHash = {])", rootHash)

        val parameters = mapOf("hash" to rootHash)
        val anchorResult =
            invoke(hashRunOperationTemplate, hashForgeOperationTemplate, hashPreApplyOperationTemplate, parameters)

        LOGGER.info("timestampHash = {}", anchorResult)
        return anchorResult.toString()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(HashTimestampingService::class.java)
    }
}