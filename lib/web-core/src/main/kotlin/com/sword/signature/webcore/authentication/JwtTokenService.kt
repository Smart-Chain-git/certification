package com.sword.signature.webcore.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime

@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.issuer}") private val jwtIssuer: String
) {
    private val algorithm = Algorithm.HMAC256(jwtSecret)


    fun generateVolatileToken(accountId: String, duration: Duration, password: String? = null) =
        generateToken(accountId = accountId, persist = false, duration = duration, password = password)

    fun generatePersistantToken(accountId: String) = generateToken(accountId = accountId, persist = true)

    /**
     * Create a JWT token from the details and return its string representation.
     */
    private fun generateToken(accountId: String, persist: Boolean, password: String? = null, duration: Duration? = null): String =
        JWT.create().apply {
            withIssuer(jwtIssuer)
            withClaim(CLAIM_PERSISTED, persist)
            withClaim(CLAIM_ID, accountId)
            withClaim(CLAIM_CREATION_TIME, OffsetDateTime.now().toString())
            if (duration != null) {
                withClaim(CLAIM_EXPIRATION_TIME, OffsetDateTime.now().plus(duration).toString())
            }
            if (password != null) {
                withClaim(CLAIM_PASSWORD, password)
            }

        }.sign(algorithm)


    /**
     * Parse the string representation of a JWT token, verify it, and return the details used to create it.
     */
    @Throws(JWTVerificationException::class)
    fun parseToken(token: String): JwtTokenDetails {
        val decodedJWT = JWT.require(algorithm)
            .withIssuer(jwtIssuer)
            .build()
            .verify(token)

        val id = decodedJWT.claims[CLAIM_ID]?.asString() ?: ""
        val creationTime = decodedJWT.claims[CLAIM_CREATION_TIME]?.asString()?.let { OffsetDateTime.parse(it) }
        val password = decodedJWT.claims[CLAIM_PASSWORD]?.asString() ?: ""
        val persisted = decodedJWT.claims[CLAIM_PERSISTED]?.asBoolean() ?: true
        val expirationTime = decodedJWT.claims[CLAIM_EXPIRATION_TIME]?.asString()?.let { OffsetDateTime.parse(it) }
        val login = decodedJWT.claims[CLAIM_LOGIN]?.asString() ?: ""

        return if (password.isBlank()) {
            LoginTokenInfo(id, creationTime!!, persisted, expirationTime!!, login)
        } else {
            ActivationToken(id, creationTime!!, persisted, expirationTime!!, password)
        }
    }

    companion object {
        private const val CLAIM_ID = "id"
        private const val CLAIM_LOGIN = "login"
        private const val CLAIM_PERSISTED = "persisted"
        private const val CLAIM_CREATION_TIME = "creationTime"
        private const val CLAIM_EXPIRATION_TIME = "expirationTime"
        private const val CLAIM_PASSWORD = "password"
    }


}
