package com.sword.signature.rsocket.mapper

import com.sword.signature.api.sign.SignRequest

fun SignRequest.toBusiness() = Pair(
    first = hash,
    second = fileName
)