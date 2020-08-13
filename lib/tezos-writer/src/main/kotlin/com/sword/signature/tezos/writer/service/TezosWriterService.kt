package com.sword.signature.tezos.writer.service

import org.ej4tezos.api.exception.TezosException
import org.ej4tezos.model.TezosIdentity

interface TezosWriterService {

    /**
     * Anchor a root hash within the Tezos blockchain.
     * @param rootHash Root hash to anchor.
     * @param signerIdentity Crypto identity of the signer.
     * @return The transactionId of the operation within the Tezos blockchain.
     */
    @Throws(TezosException::class)
    fun anchorHash(rootHash: String, signerIdentity: TezosIdentity): String

    /**
     * Retrieve the identity of the signer from its asymmetric keys.
     * @param publicKeyBase58 Signer public key in base58 string format.
     * @param secretKeyBase58 Signer secret key in base58 string format.
     * @return Crypto identity of the signer.
     */
    @Throws(TezosException::class)
    fun retrieveIdentity(publicKeyBase58: String, secretKeyBase58: String): TezosIdentity
}
