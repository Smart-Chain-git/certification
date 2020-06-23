package com.sword.signature.api.sign

data class SingleSignRequest(
    val metadata: SignMetadata,
    val hash: String
)
