package com.sword.signature.webcore.mapper

import com.sword.signature.api.sign.*
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.TreeElement


fun com.sword.signature.business.model.Account.toWeb() = Account(
    id = id,
    fullName = fullName,
    isAdmin = isAdmin,
    login = login,
    email = email,
    pubKey = pubKey
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
    transactionHash = transactionHash
)


fun TreeElement.LeafTreeElement.toWeb(proof: com.sword.signature.business.model.Proof? = null) = JobFile(
    id = id,
    hash = hash,
    jobId = jobId,
    metadata = metadata.toWeb(),
    proof = proof?.toWeb()
)

fun com.sword.signature.business.model.Proof.toWeb(): Proof {

    return Proof(
        signatureDate = signatureDate,
        filename = filename,
        algorithm = algorithm,
        publicKey = signerAddress,
        originPublicKey = creatorAddress,
        documentHash = documentHash,
        rootHash = rootHash,
        hashes = hashes.map { HashNode(it.first, it.second.name) },
        customFields = customFields,
        contractAddress = contractAddress,
        transactionHash = transactionHash,
        blockHash = blockHash
    )

}


fun Algorithm.toWeb() = com.sword.signature.api.sign.Algorithm(
    id = id,
    digestLength = digestLength,
    name = name
)
