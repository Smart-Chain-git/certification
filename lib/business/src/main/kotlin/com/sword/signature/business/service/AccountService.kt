package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch

interface AccountService {
    fun createAccount(accountDetails: AccountCreate): Account
    fun getAccount(accountId: String): Account
    fun getAccountByLoginOrEmail(loginOrEmail: String): Account
    fun getAccounts(): List<Account>
    fun patchAccount(accountId: String, accountDetails: AccountPatch): Account
    fun deleteAccount(accountId: String)
}
