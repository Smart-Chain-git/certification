package com.sword.signature.tezos.service.impl

import com.sword.signature.tezos.contract.HashTimestamping
import com.sword.signature.tezos.service.TezosWriterService
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
        tezosCryptoProvider = TezosCryptoProviderImpl()
        tezosCryptoProvider.setSecureRandom(SecureRandom())
        tezosCryptoProvider.init()

        tezosConnectivity = TezosConnectivityImpl()
        tezosConnectivity.setNodeUrl(nodeUrl)
        tezosConnectivity.init()

        tezosKeyService = TezosKeyServiceImpl()
        tezosKeyService.setTezosCryptoProvider(tezosCryptoProvider)
        tezosKeyService.init()

        tezosCoreService = TezosCoreServiceImpl()
        tezosCoreService.setTezosConnectivity(tezosConnectivity)
        tezosCoreService.init()

        tezosFeeService = TezosFeeServiceImpl()
        tezosFeeService.init()

        hashTimestamping = HashTimestamping()

        hashTimestamping.setAdminPrivateKey(TezosPrivateKey.toTezosPrivateKey(privateKey))
        hashTimestamping.setTezosContractAddress(TezosContractAddress.toTezosContractAddress(contractAddress))

        hashTimestamping.setBundleContext(null)
        hashTimestamping.setTezosKeyService(tezosKeyService)
        hashTimestamping.setTezosFeeService(tezosFeeService)
        hashTimestamping.setTezosCoreService(tezosCoreService)
        hashTimestamping.setTezosConnectivity(tezosConnectivity)
        hashTimestamping.setTezosCryptoProvider(tezosCryptoProvider)

        hashTimestamping.init()
    }

    override fun anchorHash(rootHash: String): String {
        LOGGER.debug("anchorHash (rootHash = {])", rootHash)

        return hashTimestamping.timestampHash(rootHash)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TezosWriterServiceImpl::class.java)
    }
}