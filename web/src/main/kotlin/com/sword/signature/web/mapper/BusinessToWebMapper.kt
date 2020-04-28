package com.sword.signature.web.mapper

import com.sword.signature.api.sign.Branch
import com.sword.signature.api.sign.Proof
import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.TreeElement

fun Job.toWeb() = SignResponse(jobId = id, files = files?.map { it.metadata.toWeb() } ?: emptyList())

fun FileMetadata.toWeb() = SignMetadata(
    fileName = fileName,
    fileSize = fileSize,
    fileComment = fileComment,
    batchComment = batchComment
)

fun Pair<Job, List<TreeElement>>?.toWeb(): Proof? {
    if (this == null) return null

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