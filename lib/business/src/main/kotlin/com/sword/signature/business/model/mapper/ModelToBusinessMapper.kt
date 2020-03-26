package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.entity.AlgorithmEntity

fun AccountEntity.toBusiness() = Account(
        id = id!!,
        login = login,
        email = email,
        password = password,
        fullName = fullName
)

fun AlgorithmEntity.toBusiness() = Algorithm(
        id = id!!,
        name = name,
        digestLength = digestLength
)
