package com.sword.signature.tezos.reader.service.impl

import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.TzIndexConnector
import com.sword.signature.tezos.reader.tzindex.model.TzBigMapEntry
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

    override suspend fun hashAlreadyExist(contractAddress: String, hash: String): Boolean {
        val contract = tzIndexConnector.getContract(contractAddress)
        if (contract != null && contract.bigMapIds.isNotEmpty()) {
            val bigMapId = contract.bigMapIds[0]
            tzIndexConnector.getBigMapEntry(bigMapId.toString(), hash)?.let {
                return true
            }
        }
        return false
    }

    override suspend fun getHashFromContract(contractAddress: String, hash: String): TzBigMapEntry? {
        val contract = tzIndexConnector.getContract(contractAddress)
        if (contract != null && contract.bigMapIds.isNotEmpty()) {
            val bigMapId = contract.bigMapIds[0]
            return tzIndexConnector.getBigMapEntry(bigMapId.toString(), hash)
        }
        return null
    }


}
