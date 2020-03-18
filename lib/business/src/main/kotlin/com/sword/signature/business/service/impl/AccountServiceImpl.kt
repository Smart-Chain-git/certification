package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.AccountService
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.repository.AccountRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountServiceImpl(
        private val accountRepository: AccountRepository
) : AccountService {
    @Transactional(rollbackFor = [ServiceException::class])
    override fun createAccount(accountDetails: AccountCreate): Account {
        LOGGER.trace("Creating new account.")
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

    @Transactional(rollbackFor = [ServiceException::class])
    override fun getAccount(accountId: String): Account {
        LOGGER.trace("Retrieving account with id ({}).", accountId)
        val account: Account = accountRepository.findByIdOrNull(accountId)?.toBusiness()
                ?: throw EntityNotFoundException("account", accountId)
        LOGGER.trace("Account with id ({}) retrieved.", accountId)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun getAccountByLoginOrEmail(loginOrEmail: String): Account {
        LOGGER.trace("Retrieving account with login or email ({}).", loginOrEmail)
        val account: Account = accountRepository.findFirstByLoginOrEmail(loginOrEmail)?.toBusiness()
                ?: throw AccountNotFoundException(loginOrEmail)
        LOGGER.trace("Account with id ({}) retrieved.", account)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun getAccounts(): List<Account> {
        LOGGER.trace("Retrieving all accounts.")
        val accounts: List<Account> = accountRepository.findAll().map { it.toBusiness() }
        LOGGER.trace("{} accounts retrieved.", accounts.size)
        return accounts
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun patchAccount(accountId: String, accountDetails: AccountPatch): Account {
        LOGGER.trace("Update account with id({}).", accountId)
        val account: AccountEntity = accountRepository.findByIdOrNull(accountId)
                ?: throw EntityNotFoundException("account", accountId)
        val toPatch = account.copy(
                login = accountDetails.login ?: account.login,
                email = accountDetails.email ?: account.email,
                password = accountDetails.password ?: account.password,
                fullName = accountDetails.fullName ?: account.fullName
        )
        val updatedAccount = accountRepository.save(toPatch).toBusiness()
        LOGGER.trace("Account with id ({}) updated.", accountId)
        return updatedAccount
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun deleteAccount(accountId: String) {
        LOGGER.trace("Delete account with id ({}).", accountId)
        val account: AccountEntity = accountRepository.findByIdOrNull(accountId)
                ?: throw EntityNotFoundException("account", accountId)
        accountRepository.delete(account)
        LOGGER.trace("Account with id ({}) deleted.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
