package com.sword.signature.ui.mapper

import com.sword.signature.api.sign.SignRequest

fun SignRequest.toBusiness() = Pair(
    first = hash,
    second = fileName
)