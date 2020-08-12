package com.sword.signature.business.service

import com.sword.signature.business.exception.AuthenticationException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Token
import com.sword.signature.business.model.TokenCreate
import com.sword.signature.business.model.TokenPatch
import kotlinx.coroutines.flow.Flow

interface TokenService {

    /**
     * Create a new access token.
     * @param tokenDetails Details about the token to create.
     * @return The created token.
     */
    suspend fun createToken(tokenDetails: TokenCreate): Token

    /**
     * Retrieve the list of tokens belonging to the provided account.
     * @param requester Account requesting the tokens.
     * @return The list of tokens belonging to the requester.
     */
    suspend fun findAll(requester: Account): Flow<Token>

    /**
     * Retrieve a token by its JWT token.
     * @param jwtToken JWT token of the token to retrieve.
     * @return Token if it exists, null otherwise.
     */
    suspend fun getToken(jwtToken: String): Token?

    /**
     * Retrieve a non-revoked token by its JWT token and check its validity.
     * @param jwtToken JWT token of the token to retrieve.
     * @throws AuthenticationException if the check fails.
     * @return Token if it exists and the check succeeds.
     */
    @Throws(AuthenticationException::class)
    suspend fun getAndCheckToken(jwtToken: String): Token

    /**
     * Retrieve the list of non-revoked tokens of the corresponding account.
     * @param accountId Id of the account to retrieve the tokens of.
     * @return The list of non-revoked tokens of the corresponding account.
     */
    suspend fun getTokensByAccountId(accountId: String): Flow<Token>

    /**
     * Update a token with provided fields.
     * @param requester Account requesting the update.
     * @param tokenId Id of the token to update.
     * @param tokenDetails Fields to update.
     * @return The updated token.
     */
    suspend fun patchToken(requester: Account, tokenId: String, tokenDetails: TokenPatch): Token

    /**
     * Delete the token with the provided id.
     * @param requester Account requestiong the token removal.
     * @param tokenId Id of the token to remove.
     */
    suspend fun deleteToken(requester: Account, tokenId: String)
}
