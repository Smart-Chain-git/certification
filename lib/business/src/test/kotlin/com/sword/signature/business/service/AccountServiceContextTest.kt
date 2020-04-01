package com.sword.signature.business.service

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path


class AccountServiceContextTest @Autowired constructor(
        private val accountService: AccountService,
        override val mongoTemplate: ReactiveMongoTemplate,
        override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {

    private var accountsInitialCount: Long = 0L

    private val accountId1 = "5e734ba4b075db359ea73a68"
    private val accountLogin1 = "account1"
    private val accountEmail1 = "account1@signature.com"
    private val accountPassword1 = "password"
    private val accountFullName1 = "Account 1"

    private val accountNonexistentId = "notExistentId"


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/accounts.json"))
        if (accountsInitialCount == 0L) {
            accountsInitialCount = runBlocking { mongoTemplate.getCollection("accounts").countDocuments().awaitSingle() }
        }
    }

    @Test
    fun createAccountTest() {
        val login = "test"
        val email = "test@test.com"
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)
        runBlocking {
            val createdAccount = accountService.createAccount(toCreate)

            assertAll("createdAccount",
                    { assertEquals(login, createdAccount.login) },
                    { assertEquals(email, createdAccount.email) },
                    { assertEquals(password, createdAccount.password) },
                    { assertEquals(fullName, createdAccount.fullName) }
            )
            assertEquals(accountsInitialCount + 1, mongoTemplate.getCollection("accounts").countDocuments().awaitSingle())
        }
    }

    @Test
    fun createAccountWithExistingLoginTest() {
        val login = accountLogin1
        val email = "test@test.com"
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)

        assertThrows<DuplicateKeyException> { runBlocking { accountService.createAccount(toCreate) } }
    }

    @Test
    fun createAccountWithExistingEmailTest() {
        val login = "test"
        val email = accountEmail1
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)

        assertThrows<DuplicateKeyException> { runBlocking { accountService.createAccount(toCreate) } }
    }

    @Test
    fun getAccountTest() {
        val account = runBlocking { accountService.getAccount(accountId1) }
        assertNotNull(account)
        account as Account
        assertAll("account",
                { assertEquals(accountLogin1, account?.login) },
                { assertEquals(accountEmail1, account?.email) },
                { assertEquals(accountPassword1, account?.password) },
                { assertEquals(accountFullName1, account?.fullName) })
    }

    @Test
    fun getAccountsTest() {
        val accounts = runBlocking { accountService.getAccounts().count() }

        assertEquals(accountsInitialCount, accounts.toLong())
    }

    @Test
    fun patchAccountTest() {
        val password = "newPassword"
        val toPatch = AccountPatch(password = password)

        val patchedAccount = runBlocking { accountService.patchAccount(accountId1, toPatch) }

        assertAll("patchedAccount",
                { assertEquals(accountLogin1, patchedAccount.login) },
                { assertEquals(accountEmail1, patchedAccount.email) },
                { assertEquals(password, patchedAccount.password) },
                { assertEquals(accountFullName1, patchedAccount.fullName) })
    }

    @Test
    fun patchAccountFullTest() {
        val login = "newLogin"
        val email = "new@mail.com"
        val password = "newPassword"
        val fullName = "newName"
        val toPatch = AccountPatch(login, email, password, fullName)

        val patchedAccount = runBlocking { accountService.patchAccount(accountId1, toPatch) }

        assertAll("patchedAccount",
                { assertEquals(login, patchedAccount.login) },
                { assertEquals(email, patchedAccount.email) },
                { assertEquals(password, patchedAccount.password) },
                { assertEquals(fullName, patchedAccount.fullName) })
    }

    @Test
    fun patchNonexistentAccountTest() {
        val password = "newPassword"
        val toPatch = AccountPatch(password = password)

        assertThrows<EntityNotFoundException> {
            runBlocking {
                accountService.patchAccount(
                        accountNonexistentId,
                        toPatch
                )
            }
        }
    }

    @Test
    fun deleteAccountTest() {
        runBlocking {
            accountService.deleteAccount(accountId1)

            assertEquals(
                    accountsInitialCount - 1,
                    mongoTemplate.getCollection("accounts").countDocuments().awaitSingle()
            )
        }
    }

    @Test
    fun deleteNonexistentAccountTest() {
        assertThrows<EntityNotFoundException> { runBlocking { accountService.deleteAccount(accountNonexistentId) } }
    }
}