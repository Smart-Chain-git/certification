package com.sword.signature.web.mapper

import com.sword.signature.api.sign.SignRequest
import com.sword.signature.business.model.FileHash

fun SignRequest.toBusiness() = FileHash(
    fileName = fileName,
    hash = hash,
    algorithm = algorithm
)