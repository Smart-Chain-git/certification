package com.sword.signature.webcore.mapper

import com.sword.signature.api.sign.*
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.TreeElement


fun Job.toWebSignResponse() = SignResponse(jobId = id, files = files?.map { it.metadata.toWeb() } ?: emptyList())

fun FileMetadata.toWeb() = SignMetadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)

fun Job.toWeb() = Job(
    id = id,
    createdDate = createdDate,
    injectedDate = injectedDate,
    validatedDate = validatedDate,
    numberOfTry = numberOfTry,
    blockId = blockId,
    blockDepth = blockDepth,
    algorithm = algorithm,
    flowName = flowName,
    stateDate = stateDate,
    state = state.name
)


fun TreeElement.LeafTreeElement.toWeb(proof: com.sword.signature.business.model.Proof?) = JobFile(
    id = id,
    hash = hash,
    jobId = jobId,
    metadata = metadata.toWeb(),
    proof = proof?.toWeb()
)

fun com.sword.signature.business.model.Proof.toWeb(): Proof {

    return Proof(
        algorithm = algorithm,
        publicKey = "ZpublicKey",
        originPublicKey = "ZoriginPublicKey",
        documentHash = documentHash,
        rootHash = rootHash,
        hashes = hashes.map { HashNode(it.first, it.second.name) }
    )

}


fun Algorithm.toWeb() = com.sword.signature.api.sign.Algorithm(
    id = id,
    digestLength = digestLength,
    name = name
)