package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.TokenPatch
import kotlinx.coroutines.flow.Flow

interface TokenService {

    suspend fun createToken(tokenDetails: TokenCreate): Token
    suspend fun findAll(requester: Account): Flow<Token>
    suspend fun getToken(jwtToken: String): Token?
    suspend fun getAndCheckToken(jwtToken: String): Token
    suspend fun getTokensByAccountId(accountId: String): Flow<Token>
    suspend fun patchToken(requester: Account, tokenId: String, tokenDetails: TokenPatch): Token
    suspend fun deleteToken(requester: Account, tokenId: String)
}
