package com.sword.signature.tezos.writer.service.impl

import com.sword.signature.tezos.writer.contract.HashTimestamping
import com.sword.signature.tezos.writer.service.TezosWriterService
import org.ej4tezos.api.TezosCoreService
import org.ej4tezos.api.TezosFeeService
import org.ej4tezos.api.TezosKeyService
import org.ej4tezos.crypto.impl.TezosCryptoProviderImpl
import org.ej4tezos.crypto.impl.TezosKeyServiceImpl
import org.ej4tezos.impl.TezosConnectivityImpl
import org.ej4tezos.impl.TezosCoreServiceImpl
import org.ej4tezos.impl.TezosFeeServiceImpl
import org.ej4tezos.model.TezosContractAddress
import org.ej4tezos.model.TezosPrivateKey
import org.ej4tezos.papi.TezosConnectivity
import org.ej4tezos.papi.TezosCryptoProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class TezosWriterServiceImpl(
    @Value("\${tezos.account.secret_key}") private val privateKey: String,
    @Value("\${tezos.contract.address}") private val contractAddress: String,
    @Value("\${tezos.node.url}") private val nodeUrl: String
) : TezosWriterService {
    private val tezosCryptoProvider: TezosCryptoProvider
    private val tezosConnectivity: TezosConnectivity
    private val tezosKeyService: TezosKeyService
    private val tezosCoreService: TezosCoreService
    private val tezosFeeService: TezosFeeService

    private val hashTimestamping: HashTimestamping

    init {
        tezosCryptoProvider = TezosCryptoProviderImpl().apply {
            setSecureRandom(SecureRandom())
            init()
        }

        tezosConnectivity = TezosConnectivityImpl().apply {
            setNodeUrl(nodeUrl)
            init()
        }

        tezosKeyService = TezosKeyServiceImpl().apply {
            setTezosCryptoProvider(tezosCryptoProvider)
            init()
        }

        tezosCoreService = TezosCoreServiceImpl().apply {
            setTezosConnectivity(tezosConnectivity)
            init()
        }

        tezosFeeService = TezosFeeServiceImpl().apply {
            init()
        }

        hashTimestamping = HashTimestamping().apply {
            setAdminPrivateKey(TezosPrivateKey.toTezosPrivateKey(privateKey))
            setTezosContractAddress(TezosContractAddress.toTezosContractAddress(contractAddress))

            setBundleContext(null)
            setTezosKeyService(tezosKeyService)
            setTezosFeeService(tezosFeeService)
            setTezosCoreService(tezosCoreService)
            setTezosConnectivity(tezosConnectivity)
            setTezosCryptoProvider(tezosCryptoProvider)

            init()
        }
    }

    override fun anchorHash(rootHash: String): String {
        LOGGER.debug("anchorHash (rootHash = {})", rootHash)

        return hashTimestamping.timestampHash(rootHash)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TezosWriterServiceImpl::class.java)
    }
}