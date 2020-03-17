package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.AccountService
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.repository.AccountRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountServiceImpl(
        @Autowired val accountRepository: AccountRepository
) : AccountService {
    override fun createAccount(accountDetails: AccountCreate): Account {
        LOGGER.trace("Creating new account...")
        val toCreate = AccountEntity(
                login = accountDetails.login,
                email = accountDetails.email,
                password = accountDetails.password,
                fullName = accountDetails.fullName
        )

        val createdAccount = accountRepository.save(toCreate).toBusiness()
        LOGGER.trace("New account with id ({}) created.", createdAccount.id)
        return createdAccount
    }

    override fun getAccount(accountId: String): Account {
        LOGGER.trace("Retrieving account with id ({}).", accountId)
        val account: Account = accountRepository.findByIdOrNull(accountId)?.toBusiness()
                ?: throw EntityNotFoundException("account", accountId)
        LOGGER.trace("Account with id ({}) retrieved.", accountId)
        return account
    }

    override fun getAccountByLoginOrEmail(loginOrEmail: String): Account {
        LOGGER.trace("Retrieving account with login or email ({}).", loginOrEmail)
        val account: Account = accountRepository.findFirstByLoginOrEmail(loginOrEmail)?.toBusiness()
                ?: throw AccountNotFoundException(loginOrEmail)
        LOGGER.trace("Account with id ({}) retrieved.", account)
        return account
    }

    override fun getAccounts(): List<Account> {
        TODO("Not yet implemented")
    }

    override fun patchAccount(accountId: String, accountDetails: AccountPatch): Account {
        TODO("Not yet implemented")
    }

    override fun deleteAccount(accountId: String) {
        TODO("Not yet implemented")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
