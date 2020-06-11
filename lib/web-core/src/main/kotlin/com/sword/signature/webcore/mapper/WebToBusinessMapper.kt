package com.sword.signature.webcore.mapper

import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignRequest
import com.sword.signature.business.model.FileMetadata

fun SignRequest.toBusiness() = Pair(
    first = hash.toLowerCase(),
    second = metadata.toBusiness()
)

fun SignMetadata.toBusiness() = FileMetadata(
    fileName = fileName,
    fileSize = fileSize,
    customFields = customFields
)