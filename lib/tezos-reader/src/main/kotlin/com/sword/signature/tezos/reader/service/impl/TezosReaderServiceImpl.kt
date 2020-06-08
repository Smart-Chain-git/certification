package com.sword.signature.tezos.reader.service.impl

import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.TzIndexConnector
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import org.springframework.stereotype.Service

@Service
class TezosReaderServiceImpl(
    private val tzIndexConnector: TzIndexConnector
) : TezosReaderService {

    override suspend fun getTransactionDepth(transactionHash: String): Long? {
        val transaction = getTransaction(transactionHash)

        if (transaction != null) {
            val head = tzIndexConnector.getHead()
            return head.height - transaction.height
        }

        return null
    }

    override suspend fun getTransaction(transactionHash: String): TzOp? {
        return tzIndexConnector.getTransaction(transactionHash)
    }

    override suspend fun getContract(contractAddress: String): TzContract? {
        return tzIndexConnector.getContract(contractAddress)
    }
}