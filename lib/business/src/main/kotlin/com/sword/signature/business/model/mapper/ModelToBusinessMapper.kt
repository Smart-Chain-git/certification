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
    isAdmin = isAdmin
)

fun TreeElementEntity.toBusiness(): TreeElement = when (this.type) {
    TreeElementType.NODE -> TreeElement.NodeTreeElement(
        id = id!!,
        jobId = jobId,
        hash = hash,
        parentId = parentId,
        position = position
    )
    TreeElementType.LEAF -> TreeElement.LeafTreeElement(
        id = id!!,
        jobId = jobId,
        fileName = fileName!!,
        hash = hash,
        parentId = parentId,
        position = position
    )
}


fun JobEntity.toBusiness(files: List<TreeElement.LeafTreeElement>? = null) = Job(
    id = id!!,
    algorithm = algorithm,
    userId = userId,
    blockDepth = blockDepth,
    blockId = blockId,
    createdDate = createdDate,
    injectedDate = injectedDate,
    numbreOfTry = numbreOfTry,
    validatedDate = validatedDate,
    flowName = flowName,
    state = state,
    stateDate = stateDate,
    files = files
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
        accountId = accountId,
        revoked = revoked
)