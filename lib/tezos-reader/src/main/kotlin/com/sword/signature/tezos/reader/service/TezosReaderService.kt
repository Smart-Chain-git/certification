package com.sword.signature.tezos.reader.service

import com.sword.signature.tezos.reader.tzindex.model.TzBigMapEntry
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp

interface TezosReaderService {

    /**
     * Compute the transaction depth for a given transaction hash.
     * @param transactionHash Hash of the transaction.
     * @return Transaction depth if the transaction is found in the blockchain, null otherwise.
     */
    suspend fun getTransactionDepth(transactionHash: String): Long?

    /**
     * Retrieve the transaction for a given transaction hash.
     * @param transactionHash Hash of the transaction to retrieve.
     * @return Transaction if the transaction exists in the blockchain, null otherwise.
     */
    suspend fun getTransaction(transactionHash: String): TzOp?

    /**
     * Retrieve the contract for the given address.
     * @param contractAddress Address of the smart contract.
     * @return Contract infos if the contract exists, null otherwise.
     */
    suspend fun getContract(contractAddress: String): TzContract?

    /**
     * Check if the provided hash is already in the contract storage.
     * @param contractAddress Address of the smart contract which storage may contain the provided hash.
     * @param hash Hash to check existence.
     * @return Existence of the provided hash in the given contract storage.
     */
    suspend fun hashAlreadyExist(contractAddress: String, hash: String): Boolean

    /**
     * Check if the provided hash is already in the contract storage.
     * @param contractAddress Address of the smart contract which storage may contain the provided hash.
     * @param hash Hash to get from the BigMap.
     * @return TzBigMapEntry associated to the provided hash in the given contract storage.
     */
    suspend fun getHashFromContract(contractAddress: String, hash: String): TzBigMapEntry?
}
