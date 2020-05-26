package com.sword.signature.tezos.service.impl

import com.sword.signature.tezos.tzindex.TzIndexConnector
import com.sword.signature.tezos.service.TezosReaderService
import org.springframework.stereotype.Service

@Service
class TezosReaderServiceImpl(
    private val tzIndexConnector: TzIndexConnector
) : TezosReaderService {

    override suspend fun getTransactionDepth(transactionHash: String): Long? {
        val transaction = tzIndexConnector.getTransaction(transactionHash)

        if(transaction != null) {
            val transactionHeight = transaction.height
            val head = tzIndexConnector.getHead()

            return head.height - transaction.height
        }

        return null
    }
}