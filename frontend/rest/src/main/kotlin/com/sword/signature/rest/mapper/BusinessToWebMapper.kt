package com.sword.signature.rest.mapper

import com.sword.signature.api.sign.Branch
import com.sword.signature.api.sign.Proof
import com.sword.signature.api.sign.SignResponse
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.TreeElement

fun Job.toWeb() = SignResponse(jobId = id, files = files?.map { it.fileName } ?: emptyList())

fun Pair<Job, List<TreeElement>>?.toWeb(): Proof? {
    if (this == null) return null

    val leaf = this.second.first() as TreeElement.LeafTreeElement

    return Proof(
        fileName = leaf.fileName,
        algorithm = this.first.algorithm,
        publicKey = "ZpublicKey",
        originPublicKey = "ZoriginPublicKey",
        branch =  this.second.foldRight(null as Branch?) { element, accumulator  ->
            Branch(
                hash = element.hash,
                position = element.position?.name,
                par = accumulator
            )
        }!!
    )

}