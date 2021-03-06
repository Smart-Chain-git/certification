package com.sword.signature.webcore.mapper


import com.sword.signature.api.account.Account
import com.sword.signature.api.algorithm.Algorithm
import com.sword.signature.api.check.CheckOutput
import com.sword.signature.api.job.Job
import com.sword.signature.api.job.JobFile
import com.sword.signature.api.merkel.MerkelTree
import com.sword.signature.api.proof.Proof
import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.api.token.Token
import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.TreeElement
import java.time.Duration

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
    disabled = disabled,
    firstLogin = firstLogin
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
    callBackStatus = callBackStatus.name,
    state = state.name,
    contractAddress = contractAddress,
    transactionHash = transactionHash,
    channelName = channelName,
    docsNumber = docsNumber,
    customFields = customFields,
    rootHash = rootHash,
    timestamp = timestamp
)

fun TreeElement.LeafTreeElement.toWeb(proof: com.sword.signature.business.model.Proof? = null) =
    JobFile(
        id = id,
        hash = hash,
        jobId = job.id,
        metadata = metadata.toWeb(),
        proof = proof?.toWeb(),
        jobStateName = job.state.name
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
    blockChain = blockChain,
    blockHash = blockHash,
    urls = urls.map { it.toWeb() }
)

fun com.sword.signature.business.model.URLNode.toWeb() = Proof.UrlNode(
    url = url,
    type = type.value,
    comment = comment
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
    fileId = fileId,
    jobId = jobId,
    signer = signer,
    timestamp = timestamp,
    process = trace,
    proof = proof.toWeb()
)

fun CheckException.toWeb(): CheckOutput.Ko {
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
            error = message,
            rootHash = rootHash
        )

        is CheckException.IncorrectHash -> CheckOutput.Ko(
            error = message,
            hash = hash,
            proofFileHash = proofFileHash
        )

        is CheckException.IncoherentOriginPublicKey -> CheckOutput.Ko(
            error = message,
            originPublicKey = originPublicKey,
            proofFileOriginPublicKey = proofFileOriginPublicKey
        )

        is CheckException.IncorrectSignatureDate -> CheckOutput.Ko(
            error = message,
            signatureDate = signatureDate,
            proofFileSignatureDate = proofFileSignatureDate
        )

        is CheckException.IncorrectHashAlgorithm -> CheckOutput.Ko(
            error = message,
            hash = hash,
            proofFileAlgorithm = proofFileAlgorithm
        )

        is CheckException.UnknownHashAlgorithm -> CheckOutput.Ko(
            error = message,
            proofFileAlgorithm = proofFileAlgorithm
        )

        is CheckException.IncorrectPublicKey -> CheckOutput.Ko(
            error = message,
            publicKey = publicKey,
            proofFilePublicKey = proofFilePublicKey
        )

        is CheckException.IncorrectContractAddress -> CheckOutput.Ko(
            error = message,
            contractAddress = contractAddress,
            proofFileContractAddress = proofFileContractAddress
        )
        is CheckException.TransactionNotOldEnough -> CheckOutput.Ko(
            error = message,
            currentAge = currentAge.formatToString(),
            expectedAge = expectedAge.formatToString()
        )
    }
}

fun com.sword.signature.business.model.MerkelTree.toWeb() = MerkelTree(
    algorithm = algorithm,
    root = root.toWeb()
)

fun com.sword.signature.business.model.MerkelTree.Node.toWeb(): MerkelTree.Node {
    return MerkelTree.Node(
        hash = hash,
        left = left?.toWeb(),
        right = right?.toWeb()
    )
}

fun Duration.formatToString(): String {

    val hours = toHoursPart()
    val minutes = toMinutesPart()
    val seconds = toSecondsPart()
    return  if (hours > 0) {
                "$hours h "
            } else "" +
                    if (minutes > 0) {
                        "$minutes min "
                    } else "" +
                            if (seconds > 0) {
                                "$seconds sec "
                            } else ""


}
