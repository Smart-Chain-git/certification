package com.sword.signature.business.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.sword.signature.business.model.JwtTokenDetails
import com.sword.signature.business.service.JwtTokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtTokenServiceImpl(
        @Value("\${jwt.secret}") private val jwtSecret: String,
        @Value("\${jwt.issuer}") private val jwtIssuer: String
) : JwtTokenService {
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    override fun createToken(jwtTokenDetails: JwtTokenDetails): String {
        return JWT.create()
                .withIssuer(jwtIssuer)
                .withClaim(CLAIM_ID, jwtTokenDetails.id)
                .withClaim(CLAIM_CREATION_TIME, jwtTokenDetails.creationTime)
                .sign(algorithm)
    }

    override fun parseToken(token: String): JwtTokenDetails {
        val decodedJWT = verifyToken(token)
        return JwtTokenDetails(
                id = decodedJWT.claims[CLAIM_ID]?.asString() ?: "",
                creationTime = decodedJWT.claims[CLAIM_CREATION_TIME]?.asString() ?: ""
        )
    }

    fun verifyToken(token: String): DecodedJWT {
        return JWT.require(algorithm)
                .withIssuer(jwtIssuer)
                .build()
                .verify(token)
    }

    companion object {
        private const val CLAIM_ID = "id"
        private const val CLAIM_CREATION_TIME = "creationTime"
    }
}