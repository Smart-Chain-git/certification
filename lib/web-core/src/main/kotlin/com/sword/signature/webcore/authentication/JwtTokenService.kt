package com.sword.signature.webcore.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime

@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.issuer}") private val jwtIssuer: String
) {
    private val algorithm = Algorithm.HMAC256(jwtSecret)


    fun generateVolatileToken(accountId: String, duration: Duration) =
        generateToken(accountId = accountId, persist = false, duration = duration)

    fun generatePersistantToken(accountId: String) = generateToken(accountId = accountId, persist = true)

    /**
     * Create a JWT token from the details and return its string representation.
     */
    private fun generateToken(accountId: String, persist: Boolean, duration: Duration? = null): String =
        JWT.create().apply {
            withIssuer(jwtIssuer)
            withClaim(CLAIM_PERSISTED, persist)
            withClaim(CLAIM_ID, accountId)
            withClaim(CLAIM_CREATION_TIME, OffsetDateTime.now().toString())
            if (duration != null) {
                withClaim(CLAIM_EXPIRATION_TIME, OffsetDateTime.now().plus(duration).toString())
            }

        }.sign(algorithm)


    /**
     * Parse the string representation of a JWT token, verify it, and return the details used to create it.
     */
    @Throws(JWTVerificationException::class)
    internal fun parseToken(token: String): JwtTokenDetails {
        val decodedJWT = JWT.require(algorithm)
            .withIssuer(jwtIssuer)
            .build()
            .verify(token)
        return JwtTokenDetails(
            id = decodedJWT.claims[CLAIM_ID]?.asString() ?: "",
            creationTime = decodedJWT.claims[CLAIM_CREATION_TIME]?.asString()?.let { OffsetDateTime.parse(it) },
            persisted = decodedJWT.claims[CLAIM_PERSISTED]?.asBoolean() ?: true,
            expirationTime = decodedJWT.claims[CLAIM_EXPIRATION_TIME]?.asString()?.let { OffsetDateTime.parse(it) }
        )
    }


    companion object {
        private const val CLAIM_ID = "id"
        private const val CLAIM_PERSISTED = "persisted"
        private const val CLAIM_CREATION_TIME = "creationTime"
        private const val CLAIM_EXPIRATION_TIME = "expirationTime"
    }


}