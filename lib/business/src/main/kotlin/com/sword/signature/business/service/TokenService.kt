package com.sword.signature.business.service

import com.sword.signature.business.model.Token
import kotlinx.coroutines.flow.Flow

interface TokenService {

    suspend fun getToken(token: String): Token?
    suspend fun checkAndGetToken(token: String): Token
    suspend fun getTokensByAccountId(accountId: String): Flow<Token>
}