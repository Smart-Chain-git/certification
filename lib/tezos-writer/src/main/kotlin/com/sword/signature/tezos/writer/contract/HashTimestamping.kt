package com.sword.signature.tezos.writer.contract

import org.ej4tezos.impl.TezosAbstractService
import org.slf4j.LoggerFactory

class HashTimestamping : TezosAbstractService() {

    private val hashRunOperationTemplate = loadTemplate("operations/hash_runOperation.json")
    private val hashForgeOperationTemplate = loadTemplate("operations/hash_forgeOperation.json")
    private val hashPreApplyOperationTemplate = loadTemplate("operations/hash_preApplyOperation.json")

    fun timestampHash(rootHash: String): String {
        LOGGER.debug("timestampHash call (rootHash = {})", rootHash)
        val parameters = mapOf("hash" to rootHash)

        val invokeResult =
            invoke(hashRunOperationTemplate, hashForgeOperationTemplate, hashPreApplyOperationTemplate, parameters)

        val transactionHash = invokeResult.transactionHash.toString()
        LOGGER.info("timestampHash result (rootHash = {}, transactionHash= {})", rootHash, transactionHash)

        return transactionHash
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(HashTimestamping::class.java)
    }
}