package com.sword.signature.tezos.writer.service.impl

import com.sword.signature.tezos.writer.contract.HashTimestamping
import com.sword.signature.tezos.writer.service.TezosWriterService
import org.ej4tezos.api.TezosCoreService
import org.ej4tezos.api.TezosFeeService
import org.ej4tezos.api.TezosKeyService
import org.ej4tezos.api.exception.TezosException
import org.ej4tezos.crypto.impl.TezosCryptoProviderImpl
import org.ej4tezos.crypto.impl.TezosKeyServiceImpl
import org.ej4tezos.impl.TezosConnectivityImpl
import org.ej4tezos.impl.TezosCoreServiceImpl
import org.ej4tezos.impl.TezosFeeServiceImpl
import org.ej4tezos.model.TezosConstants
import org.ej4tezos.model.TezosContractAddress
import org.ej4tezos.model.TezosIdentity
import org.ej4tezos.model.TezosPublicKey
import org.ej4tezos.papi.TezosConnectivity
import org.ej4tezos.papi.TezosCryptoProvider
import org.ej4tezos.papi.model.Ed25519KeyPair
import org.ej4tezos.utils.bytes.ByteToolbox
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class TezosWriterServiceImpl(
    @Value("\${tezos.account.publicKey}") private val publicKey: String,
    @Value("\${tezos.account.secretKey}") private val secretKey: String,
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

        val tezosIdentity: TezosIdentity = retrieveIdentity(publicKey, secretKey)

        hashTimestamping = HashTimestamping().apply {
            setAdminPrivateKey(tezosIdentity.privateKey)
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

    @Throws(TezosException::class)
    override fun anchorHash(rootHash: String): String {
        LOGGER.debug("anchorHash (rootHash = {})", rootHash)

        return hashTimestamping.timestampHash(rootHash)
    }

    @Throws(TezosException::class)
    override fun retrieveIdentity(publicKeyBase58: String, secretKeyBase58: String): TezosIdentity {
        LOGGER.debug("retrieveIdentity (publicKey = {}, secretKey = {})", publicKey, secretKey)

        // Retrieve the key pair:
        // Note that extraRawPublicKey is used to extract secret key as well due to key length consideration.
        val publicKeyBytes: ByteArray = tezosCryptoProvider.extractRawPublicKey(TezosPublicKey.toTezosPublicKey(publicKeyBase58))
        val secretKeyBytes: ByteArray = tezosCryptoProvider.extractRawPublicKey(TezosPublicKey.toTezosPublicKey(secretKeyBase58))
        val keyPair = Ed25519KeyPair.toEd25519KeyPair(publicKeyBytes, secretKeyBytes)

        // Format the public key:
        val tezosPublicKeyRaw =
            ByteToolbox.join(TezosConstants.EDPK_PREFIX, keyPair.publicKey)
        val tezosPublicKeyRawChecksum =
            tezosCryptoProvider.computeDoubleCheckSum(tezosPublicKeyRaw)
        val tezosCheckedPublicKeyRaw =
            ByteToolbox.join(tezosPublicKeyRaw, tezosPublicKeyRawChecksum)
        val tezosPublicKey =
            tezosCryptoProvider.encode(tezosCheckedPublicKeyRaw, TezosCryptoProvider.EncoderType.BASE58)

        // Format the private key:
        val tezosPrivateKeyRaw =
            ByteToolbox.join(TezosConstants.EDSK_PREFIX, keyPair.privateKey, keyPair.publicKey)
        val tezosPrivateKeyRawChecksum =
            tezosCryptoProvider.computeDoubleCheckSum(tezosPrivateKeyRaw)
        val tezosCheckedPrivateKeyRaw =
            ByteToolbox.join(tezosPrivateKeyRaw, tezosPrivateKeyRawChecksum)
        val tezosPrivateKey =
            tezosCryptoProvider.encode(tezosCheckedPrivateKeyRaw, TezosCryptoProvider.EncoderType.BASE58)

        // Format the public address:
        val digestedPublicKey =
            tezosCryptoProvider.blake2B160Digester.digest(keyPair.publicKey)
        val tezosPublicAddressRaw = ByteToolbox.join(TezosConstants.TZ1_PREFIX, digestedPublicKey)
        val tezosPublicAddressRawChecksum =
            tezosCryptoProvider.computeDoubleCheckSum(tezosPublicAddressRaw)
        val tezosPublicAddress = tezosCryptoProvider.encode(
            ByteToolbox.join(tezosPublicAddressRaw, tezosPublicAddressRawChecksum),
            TezosCryptoProvider.EncoderType.BASE58
        )

        // Instantiate the TezosIdentity with the right formatted keys and address:
        val result = TezosIdentity.toTezosIdentity(tezosPrivateKey, tezosPublicKey, tezosPublicAddress)

        // Log and return the result:
        LOGGER.debug("identify retrieved: {}", result)
        return result
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TezosWriterServiceImpl::class.java)
    }
}