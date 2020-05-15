package com.sword.signature.webcore.authentication

import java.time.OffsetDateTime

data class JwtTokenDetails(
        val id: String,
        val creationTime: OffsetDateTime?,
        val persisted: Boolean,
        val expirationTime : OffsetDateTime?
)
