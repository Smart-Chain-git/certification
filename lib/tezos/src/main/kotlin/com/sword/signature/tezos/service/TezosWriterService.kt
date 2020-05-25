package com.sword.signature.tezos.service

import org.ej4tezos.api.exception.TezosException

interface TezosWriterService {

    /**
     * @return The transactionId of the operation within the tezos blockchain.
     */
    @Throws(TezosException::class)
    fun anchorHash(rootHash: String): String
}