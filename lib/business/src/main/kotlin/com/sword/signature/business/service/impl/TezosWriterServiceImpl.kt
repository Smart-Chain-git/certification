package com.sword.signature.business.service.impl

import com.sword.signature.business.service.TezosWriterService
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
import java.security.SecureRandom

class TezosWriterServiceImpl(
    privateKey: TezosPrivateKey,
    contractAddress: TezosContractAddress,
    nodeUrl: String
) : TezosWriterService {
    private val tezosCryptoProvider: TezosCryptoProvider
    private val tezosConnectivity: TezosConnectivity
    private val tezosKeyService: TezosKeyService
    private val tezosCoreService: TezosCoreService
    private val tezosFeeService: TezosFeeService

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
    }

    override fun anchorHash(hash: String): String {
        TODO("Not yet implemented")
    }
}