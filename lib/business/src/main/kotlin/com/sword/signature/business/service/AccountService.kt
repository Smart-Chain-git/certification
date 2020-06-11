package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import kotlinx.coroutines.flow.Flow

interface AccountService {

    /**
     * Create an account within the application context.
     * @param accountDetails Details about the account to create.
     * @return The created account.
     */
    suspend fun createAccount(accountDetails: AccountCreate): Account

    /**
     * Retrieve an account by its id.
     * @param accountId Id of the account to retrieve.
     * @return The retrieved account if it exists, null otherwise.
     */
    suspend fun getAccount(accountId: String): Account?

    /**
     * Retrieve an account by its login or email.
     * @param loginOrEmail Login or email of the account to retrieve.
     * @return The retrieved account if it exists, null otherwise.
     */
    suspend fun getAccountByLoginOrEmail(loginOrEmail: String): Account?

    /**
     * Retrieve all accounts.
     * @return All accounts.
     */
    fun getAccounts(): Flow<Account>

    /**
     * Update an account with provided updates.
     * @param accountId Id ot the account to update.
     * @param accountDetails Details to update.
     * @return The updated account.
     */
    suspend fun patchAccount(accountId: String, accountDetails: AccountPatch): Account

    /**
     * Delete an account.
     * @param accountId Id of the account to delete.
     */
    suspend fun deleteAccount(accountId: String)
}
