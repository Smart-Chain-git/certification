package com.sword.signature.daemon.job

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.JobPatch
import com.sword.signature.business.model.integration.AnchorJobMessagePayload
import com.sword.signature.business.model.integration.ValidationJobMessagePayload
import com.sword.signature.business.service.AccountService
import com.sword.signature.business.service.JobService
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.daemon.configuration.TezosConfig
import com.sword.signature.daemon.logger
import com.sword.signature.daemon.sendPayload
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.writer.service.TezosWriterService
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

@Component
class AnchorJob(
    private val jobService: JobService,
    private val tezosWriterService: TezosWriterService,
    private val tezosReaderService: TezosReaderService,
    private val accountService: AccountService,
    private val anchoringRetryMessageChannel: MessageChannel,
    private val validationMessageChannel: MessageChannel,
    @Value("\${tezos.contract.address}") private val contractAddress: String,
    private val tezosConfig: TezosConfig
) {
    suspend fun anchor(payload: AnchorJobMessagePayload) {
        val requesterId = payload.requesterId
        val jobId = payload.jobId

        try {
            val requester: Account =
                accountService.getAccount(requesterId) ?: throw EntityNotFoundException("account", requesterId)
            val job = jobService.findById(requester, jobId, true)
            val rootHash = job?.rootHash ?: throw IllegalStateException("An existing job must have a root hash")

            LOGGER.debug("Check existing root hash {} on the blockchain.", rootHash)
            if (tezosReaderService.hashAlreadyExist(contractAddress, rootHash)) {
                jobService.patch(
                    requester = requester,
                    jobId = jobId,
                    patch = JobPatch(
                        state = JobStateType.REJECTED
                    )
                )
                LOGGER.info(
                    "Job '{}' got rejected due to its rootHash '{}' being already on the blockchain",
                    jobId,
                    rootHash
                )
            }

            LOGGER.debug("Ready to anchor job '{}' rootHash '{}' into the blockchain.", jobId, rootHash)

            val requesterKeys = getTezosKeys(requester.login)
            val signerIdentity =
                tezosWriterService.retrieveIdentity(
                    publicKeyBase58 = requesterKeys.first,
                    secretKeyBase58 = requesterKeys.second
                )
            val transactionHash = tezosWriterService.anchorHash(rootHash = rootHash, signerIdentity = signerIdentity)

            val injectedJob = jobService.patch(
                requester = requester,
                jobId = jobId,
                patch = JobPatch(
                    transactionHash = transactionHash,
                    state = JobStateType.INJECTED,
                    numberOfTry = job.numberOfTry + 1,
                    contractAddress = contractAddress,
                    signerAddress = signerIdentity.publicAddress.value
                )
            )
            validationMessageChannel.sendPayload(
                ValidationJobMessagePayload(
                    requesterId = requesterId,
                    jobId = jobId,
                    transactionHash = transactionHash,
                    injectionTime = injectedJob.injectedDate!!
                )
            )
        } catch (e: Exception) {
            LOGGER.error("Anchoring of job ({}) failed.", jobId, e)
            // Set the anchoring for retry later.
            anchoringRetryMessageChannel.sendPayload(payload)
            throw e
        }
    }

    private fun getTezosKeys(accountLogin: String): Pair<String, String> {
        LOGGER.debug("Retrieving keys for {}.", accountLogin)
        val accountKeys: Map<String, String> = tezosConfig.keys[accountLogin]
            ?: throw IllegalStateException("Keys for $accountLogin are not provided by configuration file.")
        val publicKey: String =
            accountKeys["publicKey"] ?: throw IllegalStateException("Public key not provided for $accountLogin.")
        val secretKey: String =
            accountKeys["secretKey"] ?: throw IllegalStateException("Secret key not provided for $accountLogin.")
        return Pair(publicKey, secretKey)
    }

    companion object {
        val LOGGER = logger()
    }
}

