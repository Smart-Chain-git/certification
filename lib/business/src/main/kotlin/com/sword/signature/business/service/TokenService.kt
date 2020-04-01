package com.sword.signature.business.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.sword.signature.business.model.TokenDetails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenService(
        @Value("\${jwt.secret}") private val jwtSecret: String,
        @Value("\${jwt.issuer}") private val jwtIssuer: String
) {
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    fun createToken(tokenDetails: TokenDetails): String {
        return JWT.create()
                .withIssuer(jwtIssuer)
                .withClaim(CLAIM_ID, tokenDetails.id)
                .withClaim(CLAIM_LOGIN, tokenDetails.login)
                .sign(algorithm)
    }

    fun parseToken(token: String): TokenDetails {
        val decodedJWT = verifyToken(token)
        return TokenDetails(
                id = decodedJWT.claims[CLAIM_ID]?.asString() ?: "",
                login = decodedJWT.claims[CLAIM_LOGIN]?.asString() ?: ""
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
        private const val CLAIM_LOGIN = "login"
    }

}