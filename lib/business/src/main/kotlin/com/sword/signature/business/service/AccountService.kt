package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import kotlinx.coroutines.flow.Flow

interface AccountService {
    suspend fun createAccount(accountDetails: AccountCreate): Account
    suspend fun getAccount(accountId: String): Account?
    suspend fun getAccountByLoginOrEmail(loginOrEmail: String): Account?
    fun getAccounts(): Flow<Account>
    suspend fun patchAccount(accountId: String, accountDetails: AccountPatch): Account
    suspend fun deleteAccount(accountId: String)
}
