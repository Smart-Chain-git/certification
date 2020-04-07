package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.ServiceException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.business.model.mapper.toBusiness
import com.sword.signature.business.service.AccountService
import com.sword.signature.model.entity.AccountEntity
import com.sword.signature.model.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountServiceImpl(
        private val accountRepository: AccountRepository
) : AccountService {
    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun createAccount(accountDetails: AccountCreate): Account {
        LOGGER.trace("Creating new account.")
        val toCreate = AccountEntity(
                login = accountDetails.login,
                email = accountDetails.email,
                password = accountDetails.password,
                fullName = accountDetails.fullName
        )

        val createdAccount = accountRepository.save(toCreate).awaitSingle().toBusiness()
        LOGGER.trace("New account with id ({}) created.", createdAccount.id)
        return createdAccount
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getAccount(accountId: String): Account? {
        LOGGER.trace("Retrieving account with id ({}).", accountId)
        val account = accountRepository.findById(accountId).awaitFirstOrNull()?.toBusiness()
        LOGGER.trace("Account with id ({}) retrieved.", accountId)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getAccountByLoginOrEmail(loginOrEmail: String): Account? {
        LOGGER.trace("Retrieving account with login or email ({}).", loginOrEmail)
        val account = accountRepository.findFirstByLoginOrEmail(loginOrEmail)?.toBusiness()
        LOGGER.trace("Account with id ({}) retrieved.", account)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun getAccounts(): Flow<Account> {
        LOGGER.trace("Retrieving all accounts.")
        return accountRepository.findAll().asFlow().map { it.toBusiness() }
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun patchAccount(accountId: String, accountDetails: AccountPatch): Account {
        LOGGER.trace("Update account with id({}).", accountId)
        val account: AccountEntity = accountRepository.findById(accountId).awaitFirstOrNull()
                ?: throw EntityNotFoundException("account", accountId)
        val toPatch = account.copy(
                login = accountDetails.login ?: account.login,
                email = accountDetails.email ?: account.email,
                password = accountDetails.password ?: account.password,
                fullName = accountDetails.fullName ?: account.fullName
        )
        val updatedAccount = accountRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.trace("Account with id ({}) updated.", accountId)
        return updatedAccount
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun deleteAccount(accountId: String) {
        LOGGER.trace("Delete account with id ({}).", accountId)
        val account: AccountEntity = accountRepository.findById(accountId).awaitFirstOrNull()
                ?: throw EntityNotFoundException("account", accountId)
        accountRepository.delete(account).awaitFirstOrNull()
        LOGGER.trace("Account with id ({}) deleted.", accountId)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
