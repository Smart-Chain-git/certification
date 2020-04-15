package com.sword.signature.business.service

import com.sword.signature.business.model.Token
import com.sword.signature.business.model.TokenCreate
import kotlinx.coroutines.flow.Flow

interface TokenService {

    suspend fun createToken(tokenDetails: TokenCreate): Token
    suspend fun getToken(token: String): Token?
    suspend fun checkAndGetToken(token: String): Token
    suspend fun getTokensByAccountId(accountId: String): Flow<Token>
    suspend fun deleteToken(tokenId: String)
}