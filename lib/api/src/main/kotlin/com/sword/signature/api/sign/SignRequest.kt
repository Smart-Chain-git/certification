package com.sword.signature.api.sign

data class SignRequest(
    val metadata: SignMetadata,
    val hash: String
)
