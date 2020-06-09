package com.sword.signature.tezos.reader.service

import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp

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

    /**
     * Retrieve the contract for the given address.
     * @return Contract infos if the contract exists, null otherwise.
     */
    suspend fun getContract(contractAddress: String): TzContract?
}