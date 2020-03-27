package com.sword.signature.business.service.impl

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.FileHash
import com.sword.signature.business.model.SignJob
import com.sword.signature.business.service.SignService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class SignServiceImpl(
    @Value("\${sign.tree.maximunLeaf}") val maximunLeaf: Int
) : SignService {
    override fun batchSign(account: Account, fileHashs: Flow<FileHash>): Flow<SignJob> {

        return flow {

            val intermediary = mutableListOf<FileHash>()
            fileHashs.collect {  fileHash ->
                LOGGER.debug("fichier {}", fileHash.fileName)
                intermediary.add(fileHash)
                if (intermediary.size >= maximunLeaf) {
                    LOGGER.debug("intermed {}", intermediary.size)
                    emit(anchorTree(intermediary))
                    intermediary.clear()
                }
            }
            // emission des derniers hash
            if(intermediary.isNotEmpty()) {
                emit(anchorTree(intermediary))
            }
        }
    }

    private fun anchorTree(fileHashs: List<FileHash>): SignJob {
        LOGGER.debug("appel arbre pour {}", fileHashs)
        return SignJob(
            id = UUID.randomUUID().toString(),
            files = fileHashs.map { it.fileName }
        )
    }


    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SignServiceImpl::class.java)
    }

}