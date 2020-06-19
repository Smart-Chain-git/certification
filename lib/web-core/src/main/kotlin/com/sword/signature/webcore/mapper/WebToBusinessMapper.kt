package com.sword.signature.webcore.mapper

import com.sword.signature.api.proof.Proof
import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignRequest
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.common.enums.TreeElementPosition

fun SignRequest.toBusiness() = Pair(
    first = hash.toLowerCase(),
    second = metadata.toBusiness()
)

fun SignMetadata.toBusiness() = FileMetadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)

fun Proof.toBusiness() = com.sword.signature.business.model.Proof(
    signatureDate = signatureDate,
    filename = filename,
    rootHash = rootHash,
    documentHash = documentHash,
    hashes = hashes.map { it.toBusiness() },
    algorithm = algorithm,
    customFields = customFields,
    contractAddress = contractAddress,
    transactionHash = transactionHash,
    blockHash = blockHash,
    signerAddress = signerAddress,
    creatorAddress = creatorAddress
)

fun Proof.HashNode.toBusiness() = Pair(
    first = hash,
    second = TreeElementPosition.valueOf(position)
)
