package com.sword.signature.business.service.impl

import com.sword.signature.business.exception.DuplicateException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.exception.MissingRightException
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
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository
) : AccountService {
    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun createAccount(requester: Account, accountDetails: AccountCreate): Account {
        LOGGER.debug("Creating new account.")

        // Check rights to perform operation.
        if(!requester.isAdmin) {
            throw MissingRightException(requester)
        }

        val toCreate = AccountEntity(
            login = accountDetails.login,
            email = accountDetails.email,
            password = accountDetails.password,
            fullName = accountDetails.fullName,
            company = accountDetails.company,
            country = accountDetails.country,
            publicKey = accountDetails.publicKey,
            hash = accountDetails.hash,
            isAdmin = accountDetails.isAdmin,
            disabled = accountDetails.disabled
        )

        try {
            val createdAccount = accountRepository.save(toCreate).awaitSingle().toBusiness()
            LOGGER.debug("New account with id ({}) created.", createdAccount.id)
            return createdAccount
        } catch(e: DuplicateKeyException) {
            throw DuplicateException(e.toString())
        } catch (e: Exception) {
            throw e
        }
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getAccount(accountId: String): Account? {
        LOGGER.debug("Retrieving account with id ({}).", accountId)
        val account = accountRepository.findById(accountId).awaitFirstOrNull()?.toBusiness()
        LOGGER.debug("Account with id ({}) retrieved.", accountId)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun getAccountByLoginOrEmail(loginOrEmail: String): Account? {
        LOGGER.debug("Retrieving account with login or email ({}).", loginOrEmail)
        val account = accountRepository.findFirstByLoginOrEmail(loginOrEmail)?.toBusiness()
        LOGGER.debug("Account with id ({}) retrieved.", account)
        return account
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override fun getAccounts(): Flow<Account> {
        LOGGER.debug("Retrieving all accounts.")
        return accountRepository.findAll().asFlow().map { it.toBusiness() }
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun patchAccount(requester: Account, accountId: String, accountDetails: AccountPatch): Account {
        LOGGER.debug("Update account with id({}).", accountId)

        // Check rights to perform operation.
        if(!requester.isAdmin && requester.id != accountId) {
            throw MissingRightException(requester)
        }

        val account: AccountEntity = accountRepository.findById(accountId).awaitFirstOrNull()
            ?: throw EntityNotFoundException("account", accountId)
        val toPatch = account.copy(
            login = accountDetails.login ?: account.login,
            email = accountDetails.email ?: account.email,
            password = accountDetails.password ?: account.password,
            fullName = accountDetails.fullName ?: account.fullName,
            company = accountDetails.company ?: account.company,
            country = accountDetails.country ?: account.country,
            publicKey = accountDetails.publicKey ?: account.publicKey,
            hash = accountDetails.hash ?: account.hash,
            isAdmin = accountDetails.isAdmin ?: account.isAdmin,
            disabled = accountDetails.disabled ?: account.disabled
        )
        val updatedAccount = accountRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.debug("Account with id ({}) updated.", accountId)
        return updatedAccount
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun deleteAccount(requester: Account, accountId: String) {
        LOGGER.debug("Delete account with id ({}).", accountId)

        // Check rights to perform operation.
        if(!requester.isAdmin && requester.id != accountId) {
            throw MissingRightException(requester)
        }

        val account: AccountEntity = accountRepository.findById(accountId).awaitFirstOrNull()
            ?: throw EntityNotFoundException("account", accountId)
        accountRepository.delete(account).awaitFirstOrNull()
        LOGGER.debug("Account with id ({}) deleted.", accountId)
    }

    @Transactional(rollbackFor = [ServiceException::class])
    override suspend fun activateAccount(requester: Account, password: String): Account {
        val accountId = requester.id
        LOGGER.debug("Activate account with id ({}).", accountId)

        // Check rights to perform operation.
        if(!requester.isAdmin && requester.id != accountId) {
            throw MissingRightException(requester)
        }

        val account: AccountEntity = accountRepository.findById(accountId).awaitFirstOrNull()
            ?: throw EntityNotFoundException("account", accountId)

        val toPatch = account.copy(
            login = account.login,
            email = account.email,
            password = password,
            fullName = account.fullName,
            company = account.company,
            country = account.country,
            publicKey = account.publicKey,
            hash = account.hash,
            isAdmin = account.isAdmin,
            disabled = account.disabled,
            firstLogin = false
        )

        val updatedAccount = accountRepository.save(toPatch).awaitSingle().toBusiness()
        LOGGER.debug("Account with id ({}) activated.", accountId)

        return updatedAccount
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AccountServiceImpl::class.java)
    }
}
