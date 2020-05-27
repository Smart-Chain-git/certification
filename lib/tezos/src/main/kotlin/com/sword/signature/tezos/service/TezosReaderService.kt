package com.sword.signature.tezos.service

import com.sword.signature.tezos.tzindex.model.TzOp

interface TezosReaderService {

    /**
     * Compute the transaction depth for a given transaction hash.
     * @return Transaction depth if the transaction is found in the blockchain, null otherwise.
     */
    suspend fun getTransactionDepth(transactionHash: String): Long?

    /**
     * Retrieve the transaction for a given transaction hash.
     * @return Transaction if the transaction exists in the blockchain, null otherwise.
     */
    suspend fun getTransaction(transactionHash: String): TzOp?
}