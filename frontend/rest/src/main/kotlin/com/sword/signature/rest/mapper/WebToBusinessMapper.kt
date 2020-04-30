package com.sword.signature.rest.mapper

import com.sword.signature.api.sign.SignRequest

fun SignRequest.toBusiness() = Pair(
    first = hash,
    second = fileName
)