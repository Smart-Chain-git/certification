package com.sword.signature.tezos.service.impl

import com.sword.signature.tezos.service.TezosReaderService
import com.sword.signature.tezos.tzindex.TzIndexConnector
import com.sword.signature.tezos.tzindex.model.TzOp
import org.springframework.stereotype.Service

@Service
class TezosReaderServiceImpl(
    private val tzIndexConnector: TzIndexConnector
) : TezosReaderService {

    override suspend fun getTransactionDepth(transactionHash: String): Long? {
        val transaction = getTransaction(transactionHash)

        if (transaction != null) {
            val transactionHeight = transaction.height
            val head = tzIndexConnector.getHead()

            return head.height - transaction.height
        }

        return null
    }

    override suspend fun getTransaction(transactionHash: String): TzOp? {
        return tzIndexConnector.getTransaction(transactionHash)
    }
}