package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.entity.AlgorithmEntity

fun Account.toModel() = AccountEntity(
        id = id,
        login = login,
        email = email,
        password = password,
        fullName = fullName,
        isAdmin = isAdmin
)

fun Algorithm.toModel() = AlgorithmEntity(
        id = id,
        name = name,
        digestLength = digestLength
)
