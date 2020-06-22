package com.sword.signature.webcore.mapper


import com.sword.signature.api.account.Account
import com.sword.signature.api.algorithm.Algorithm
import com.sword.signature.api.check.CheckOutput
import com.sword.signature.api.job.Job
import com.sword.signature.api.job.JobFile
import com.sword.signature.api.proof.Proof
import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.api.token.Token
import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.TreeElement

fun com.sword.signature.business.model.Account.toWeb() = Account(
    id = id,
    fullName = fullName,
    login = login,
    email = email,
    company = company,
    country = country,
    publicKey = publicKey,
    hash = hash,
    isAdmin = isAdmin,
    disabled = disabled
)


fun com.sword.signature.business.model.Job.toWebSignResponse() =
    SignResponse(jobId = id, files = files?.map { it.metadata.toWeb() } ?: emptyList())

fun FileMetadata.toWeb() = SignMetadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)

fun com.sword.signature.business.model.Job.toWeb() = Job(
    id = id,
    createdDate = createdDate,
    injectedDate = injectedDate,
    validatedDate = validatedDate,
    numberOfTry = numberOfTry,
    blockHash = blockHash,
    blockDepth = blockDepth,
    algorithm = algorithm,
    flowName = flowName,
    stateDate = stateDate,
    state = state.name,
    contractAddress = contractAddress,
    transactionHash = transactionHash,
    channelName = channelName,
    docsNumber = docsNumber,
    customFields = customFields
)

fun TreeElement.LeafTreeElement.toWeb(proof: com.sword.signature.business.model.Proof? = null) =
    JobFile(
        id = id,
        hash = hash,
        jobId = jobId,
        metadata = metadata.toWeb(),
        proof = proof?.toWeb()
    )

fun com.sword.signature.business.model.Proof.toWeb() = Proof(
    signatureDate = signatureDate,
    filename = filename,
    algorithm = algorithm,
    signerAddress = signerAddress,
    creatorAddress = creatorAddress,
    documentHash = documentHash,
    rootHash = rootHash,
    hashes = hashes.map { Proof.HashNode(it.first, it.second.name) },
    customFields = customFields,
    contractAddress = contractAddress,
    transactionHash = transactionHash,
    blockHash = blockHash
)

fun com.sword.signature.business.model.Algorithm.toWeb() =
    Algorithm(
        id = id,
        digestLength = digestLength,
        name = name
    )

fun com.sword.signature.business.model.Token.toWeb() = Token(
    id = id,
    name = name,
    jwtToken = jwtToken,
    creationDate = creationDate,
    expirationDate = expirationDate,
    accountId = accountId,
    revoked = revoked
)

fun com.sword.signature.business.model.CheckResponse.toWeb() = CheckOutput.Ok(
    status = status,
    signer = signer,
    timestamp = timestamp,
    process = trace,
    proof = proof.toWeb()
)

fun com.sword.signature.business.exception.CheckException.toWeb(): CheckOutput.Ko {
    return when (this) {
        is CheckException.HashNotPresent -> CheckOutput.Ko(
            error = message,
            documentHash = documentHash
        )
        is CheckException.HashInconsistent -> CheckOutput.Ko(
            error = message,
            documentHash = documentHash,
            proofDocumentHash = proofDocumentHash
        )
        is CheckException.IncorrectRootHash -> CheckOutput.Ko(
            error = message,
            documentHash = documentHash,
            proofDocumentHash = proofDocumentHash
        )
        is CheckException.UnknownRootHash -> CheckOutput.Ko(
            error = message,
            rootHash = rootHash
        )
        is CheckException.DocumentKnownUnknownRootHash -> CheckOutput.Ko(
            error = message,
            signer = signer,
            publicKey = publicKey,
            date = date
        )
        is CheckException.IncorrectProofFile -> CheckOutput.Ko(
            error = message
        )
        is CheckException.IncorrectTransaction -> CheckOutput.Ko(
            error = message
        )
        is CheckException.TransactionNotDeepEnough -> CheckOutput.Ko(
            error = message,
            currentDepth = currentDepth,
            expectedDepth = expectedDepth
        )
        is CheckException.IncoherentData -> CheckOutput.Ko(
            error = message
        )
        is CheckException.NoTransaction -> CheckOutput.Ko(
            error = message
        )
    }
}
