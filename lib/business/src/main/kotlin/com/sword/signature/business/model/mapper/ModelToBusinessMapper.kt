package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.*
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.model.entity.*

fun AccountEntity.toBusiness() = Account(
    id = id!!,
    login = login,
    email = email,
    password = password,
    fullName = fullName,
    company = company,
    country = country,
    publicKey = publicKey,
    hash = hash,
    isAdmin = isAdmin,
    disabled = disabled,
    firstLogin = firstLogin
)

fun TreeElementEntity.Metadata.toBusiness() = FileMetadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)

fun TreeElementEntity.toBusiness(job: Job): TreeElement = when (this.type) {
    TreeElementType.NODE -> TreeElement.NodeTreeElement(
        id = id!!,
        job = job,
        hash = hash,
        parentId = parentId,
        position = position
    )
    TreeElementType.LEAF -> TreeElement.LeafTreeElement(
        id = id!!,
        job = job,
        metadata = metadata!!.toBusiness(),
        hash = hash,
        parentId = parentId,
        position = position
    )
}

fun JobEntity.toBusiness(rootHash: String? = null, files: List<TreeElement.LeafTreeElement>? = null) = Job(
    id = id!!,
    algorithm = algorithm,
    userId = userId,
    transactionHash = transactionHash,
    blockDepth = blockDepth,
    blockHash = blockHash,
    createdDate = createdDate,
    injectedDate = injectedDate,
    numberOfTry = numberOfTry,
    validatedDate = validatedDate,
    flowName = flowName,
    state = state,
    stateDate = stateDate,
    rootHash = rootHash,
    files = files,
    callBackUrl = callBackUrl,
    contractAddress = contractAddress,
    channelName = channelName,
    docsNumber = docsNumber,
    signerAddress = signerAddress,
    customFields = customFields
)

fun AlgorithmEntity.toBusiness() = Algorithm(
    id = id!!,
    name = name,
    digestLength = digestLength
)

fun TokenEntity.toBusiness() = Token(
    id = id!!,
    name = name,
    jwtToken = jwtToken,
    expirationDate = expirationDate,
    creationDate = creationDate,
    accountId = accountId,
    revoked = revoked
)
