package com.sword.signature.business.service

import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import java.nio.file.Path


class AccountServiceContextTest @Autowired constructor(
        private val accountService: AccountService,
        override val mongoTemplate: MongoTemplate
) : AbstractServiceContextTest() {

    private val accountsInitialCount: Long = 3

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
    }

    @Test
    fun createAccountTest() {
        val login = "test"
        val email = "test@test.com"
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)

        val createdAccount = accountService.createAccount(toCreate)


        assertAll("createdAccount",
                { assertEquals(login, createdAccount.login) },
                { assertEquals(email, createdAccount.email) },
                { assertEquals(password, createdAccount.password) },
                { assertEquals(fullName, createdAccount.fullName) }
        )
        assertEquals(accountsInitialCount + 1, mongoTemplate.getCollection("accounts").countDocuments())
    }

    @Test
    fun createAccountWithExistingLoginTest() {
        val login = accountLogin1
        val email = "test@test.com"
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)

        assertThrows<DuplicateKeyException> { accountService.createAccount(toCreate) }
    }

    @Test
    fun createAccountWithExistingEmailTest() {
        val login = "test"
        val email = accountEmail1
        val password = "secured"
        val fullName = "fullName"
        val toCreate = AccountCreate(login, email, password, fullName)

        assertThrows<DuplicateKeyException> { accountService.createAccount(toCreate) }
    }

    @Test
    fun getAccountTest() {
        val account = accountService.getAccount(accountId1)

        assertAll("account",
                { assertEquals(accountLogin1, account?.login) },
                { assertEquals(accountEmail1, account?.email) },
                { assertEquals(accountPassword1, account?.password) },
                { assertEquals(accountFullName1, account?.fullName) })
    }

    @Test
    fun getAccountsTest() {
        val accounts = accountService.getAccounts()

        assertEquals(3, accounts.size)
    }

    @Test
    fun patchAccountTest() {
        val password = "newPassword"
        val toPatch = AccountPatch(password = password)

        val patchedAccount = accountService.patchAccount(accountId1, toPatch)

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

        val patchedAccount = accountService.patchAccount(accountId1, toPatch)

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

        assertThrows<EntityNotFoundException> { accountService.patchAccount(accountNonexistentId, toPatch) }
    }

    @Test
    fun deleteAccountTest() {
        accountService.deleteAccount(accountId1)

        assertEquals(accountsInitialCount - 1, mongoTemplate.getCollection("accounts").countDocuments())
    }

    @Test
    fun deleteNonexistentAccountTest() {
        assertThrows<EntityNotFoundException> { accountService.deleteAccount(accountNonexistentId) }
    }
}