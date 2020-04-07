package com.sword.signature.business.service

import com.sword.signature.business.model.Token


interface TokenService {

    suspend fun getToken(jwtToken: String): Token?
    suspend fun checkAndGetToken(jwtToken: String): Token
}