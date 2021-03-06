package com.sword.signature.business.model.mapper

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Algorithm
import com.sword.signature.business.model.FileMetadata
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.entity.AlgorithmEntity
import com.sword.signature.model.entity.TreeElementEntity

fun Account.toModel() = AccountEntity(
    id = id,
    login = login,
    email = email,
    password = password,
    fullName = fullName,
    company = company,
    country = country,
    publicKey = publicKey,
    hash = hash,
    isAdmin = isAdmin
)

fun Algorithm.toModel() = AlgorithmEntity(
    id = id,
    name = name,
    digestLength = digestLength
)

fun FileMetadata.toModel() = TreeElementEntity.Metadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)
