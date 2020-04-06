package com.sword.signature.business.service

import com.sword.signature.business.model.JwtTokenDetails

/**
 * JWT token service dedicated to JWT tokens creation and verification.
 */
interface JwtTokenService {

    /**
     * Create a JWT token from the details and return its string representation.
     */
    fun createToken(jwtTokenDetails: JwtTokenDetails): String

    /**
     * Parse the string representation of a JWT token, verify it, and return the details used to create it.
     */
    fun parseToken(token: String): JwtTokenDetails
}