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

fun Job.toWeb() = com.sword.signature.api.sign.Job(
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


fun TreeElement.LeafTreeElement.toWeb(proof: Pair<Job, List<TreeElement>>?) = JobFile(
    id = id,
    hash = hash,
    jobId = jobId,
    metadata = metadata.toWeb(),
    proof = proof?.toWeb()
)

fun Pair<Job, List<TreeElement>>.toWeb(): Proof {

    val leaf = this.second.first() as TreeElement.LeafTreeElement

    return Proof(
        metadata = leaf.metadata.toWeb(),
        algorithm = this.first.algorithm,
        publicKey = "ZpublicKey",
        originPublicKey = "ZoriginPublicKey",
        branch = this.second.foldRight(null as Branch?) { element, accumulator ->
            Branch(
                hash = element.hash,
                position = element.position?.name,
                par = accumulator
            )
        }!!
    )

}


fun Algorithm.toWeb() = com.sword.signature.api.sign.Algorithm(
    id = id,
    digestLength = digestLength,
    name = name
)