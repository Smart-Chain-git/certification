package com.sword.signature.webcore.authentication

import java.time.OffsetDateTime

sealed class JwtTokenDetails {
        abstract val id: String
        abstract val creationTime: OffsetDateTime?
        abstract val persisted: Boolean
        abstract val expirationTime: OffsetDateTime?
}

data class LoginTokenInfo(override val id : String,
                          override val creationTime : OffsetDateTime?,
                          override val persisted: Boolean,
                          override val expirationTime : OffsetDateTime?,
                          val login: String) : JwtTokenDetails()

data class ActivationToken(override val id : String,
                           override val creationTime : OffsetDateTime?,
                           override val persisted: Boolean,
                           override val expirationTime : OffsetDateTime?,
                           val password: String) : JwtTokenDetails()
