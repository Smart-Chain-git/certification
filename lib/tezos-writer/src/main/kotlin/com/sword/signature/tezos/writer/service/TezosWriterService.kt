package com.sword.signature.tezos.writer.service

import org.ej4tezos.api.exception.TezosException
import org.ej4tezos.model.TezosIdentity

interface TezosWriterService {

    /**
     * @return The transactionId of the operation within the tezos blockchain.
     */
    @Throws(TezosException::class)
    fun anchorHash(rootHash: String, signerIdentity: TezosIdentity): String

    @Throws(TezosException::class)
    fun retrieveIdentity(publicKeyBase58: String, secretKeyBase58: String): TezosIdentity
}