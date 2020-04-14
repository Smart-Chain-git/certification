package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.Token
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.entity.AlgorithmEntity
import com.sword.signature.model.entity.JobEntity
import com.sword.signature.model.entity.TokenEntity

fun AccountEntity.toBusiness() = Account(
        id = id!!,
        login = login,
        email = email,
        password = password,
        fullName = fullName,
        isAdmin = isAdmin
)

fun JobEntity.toBusiness(files: List<String>? = null) = Job(
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
        accountId = accountId
)