package com.sword.signature.webcore.mapper

import com.sword.signature.api.sign.SignMetadata
import com.sword.signature.api.sign.SignRequest
import com.sword.signature.business.model.FileMetadata

fun SignRequest.toBusiness() = Pair(
    first = hash,
    second = metadata.toBusiness()
)

fun SignMetadata.toBusiness() = FileMetadata(
    fileName = fileName,
    fileSize = fileSize,
    fileComment = fileComment,
    batchComment = batchComment
)