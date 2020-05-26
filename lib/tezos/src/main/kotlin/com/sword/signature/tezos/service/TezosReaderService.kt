package com.sword.signature.tezos.service

interface TezosReaderService {

    suspend fun getTransactionDepth(transactionHash: String): Long?
}