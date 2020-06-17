package com.sword.signature.business.service

import com.sword.signature.business.exception.DuplicateException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.model.AccountPatch
import com.sword.signature.model.migration.MigrationHandler
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path


class AccountServiceContextTest @Autowired constructor(
    private val accountService: AccountService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler: MigrationHandler
) : AbstractServiceContextTest() {

    private val nonAdminAccount = Account(
        id = "nonAdmin",
        login = "nonAdminLogin",
        password = "nonAdminPassword",
        fullName = "nonAdmin",
        email = "nonAdmin@signature.com",
        company = null,
        country = null,
        publicKey = null,
        hash = null,
        isAdmin = false,
        disabled = false
    )

    private val adminAccount = Account(
        id = "admin",
        login = "adminLogin",
        password = "adminPassword",
        fullName = "admin",
        email = "admin@signature.com",
        company = null,
        country = null,
        publicKey = null,
        hash = null,
        isAdmin = true,
        disabled = false
    )

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
        importJsonDatasets(Path.of("src/test/resources/datasets/accounts.json"))
        if (accountsInitialCount == 0L) {
            accountsInitialCount =
                runBlocking { mongoTemplate.getCollection("accounts").awaitSingle().countDocuments().awaitSingle() }
        }
    }

    @Nested
    inner class CreateAccountTest {

        private val newLogin = "test"
        private val newEmail = "test@test.com"
        private val newPassword = "secured"
        private val newFullName = "fullName"
        private val newCompany = "Great Company"
        private val newCountry = "France"
        private val newPublicKey = "edpkvGfYw3LyB1UcCahKQk4rF2tvbMUk8GFiTuMjL75uGXrpvKXhjn"
        private val newHash = "tz1YEuW1erL1kXhUKuy7NLYrbzxQsPcn46wQ"

        @Test
        fun createAccountTest() {
            val toCreate = AccountCreate(
                login = newLogin, email = newEmail, password = newPassword, fullName = newFullName,
                company = newCompany, country = newCountry, publicKey = newPublicKey, hash = newHash
            )
            runBlocking {
                val createdAccount = accountService.createAccount(adminAccount, toCreate)

                assertAll("createdAccount",
                    { assertEquals(newLogin, createdAccount.login) },
                    { assertEquals(newEmail, createdAccount.email) },
                    { assertEquals(newPassword, createdAccount.password) },
                    { assertEquals(newFullName, createdAccount.fullName) },
                    { assertEquals(newCompany, createdAccount.company) },
                    { assertEquals(newCountry, createdAccount.country) },
                    { assertEquals(newPublicKey, createdAccount.publicKey) },
                    { assertEquals(newHash, createdAccount.hash) }
                )
                assertEquals(
                    accountsInitialCount + 1,
                    mongoTemplate.getCollection("accounts").awaitSingle().countDocuments().awaitSingle()
                )
            }
        }

        @Test
        fun createAccountWithExistingLoginTest() {
            val login = accountLogin1
            val toCreate = AccountCreate(
                login = login, email = newEmail, password = newPassword, fullName = newFullName,
                company = newCompany, country = newCountry, publicKey = newPublicKey, hash = newHash
            )

            assertThrows<DuplicateException> { runBlocking { accountService.createAccount(adminAccount, toCreate) } }
        }

        @Test
        fun createAccountWithExistingEmailTest() {
            val email = accountEmail1
            val toCreate = AccountCreate(
                login = newLogin, email = email, password = newPassword, fullName = newFullName,
                company = newCompany, country = newCountry, publicKey = newPublicKey, hash = newHash
            )

            assertThrows<DuplicateException> { runBlocking { accountService.createAccount(adminAccount, toCreate) } }
        }
    }

    @Test
    fun getAccountTest() {
        val account = runBlocking { accountService.getAccount(accountId1) }
        assertNotNull(account)
        account as Account
        assertAll("account",
            { assertEquals(accountLogin1, account.login) },
            { assertEquals(accountEmail1, account.email) },
            { assertEquals(accountPassword1, account.password) },
            { assertEquals(accountFullName1, account.fullName) })
    }

    @Test
    fun getAccountsTest() {
        val accounts = runBlocking { accountService.getAccounts().count() }

        assertEquals(accountsInitialCount, accounts.toLong())
    }

    @Nested
    inner class PatchAccountTest {

        private val newLogin = "newLogin"
        private val newEmail = "newEmail"
        private val newPassword = "newPassword"
        private val newFullName = "newFullName"
        private val newCompany = "newCompany"
        private val newCountry = "newCountry"
        private val newPublicKey = "newPublicKey"
        private val newHash = "newHash"
        private val newIsAdmin = true
        private val newDisabled = true

        @Test
        fun patchAccountTest() {
            val toPatch = AccountPatch(password = newPassword)

            val patchedAccount = runBlocking { accountService.patchAccount(adminAccount,accountId1, toPatch) }

            assertAll("patchedAccount",
                { assertEquals(accountLogin1, patchedAccount.login) },
                { assertEquals(accountEmail1, patchedAccount.email) },
                { assertEquals(newPassword, patchedAccount.password) },
                { assertEquals(accountFullName1, patchedAccount.fullName) })
        }

        @Test
        fun patchAccountFullTest() {
            val toPatch = AccountPatch(
                login = newLogin, email = newEmail, password = newPassword, fullName = newFullName,
                company = newCompany, country = newCountry, publicKey = newPublicKey, hash = newHash,
                isAdmin = newIsAdmin, disabled = newDisabled
            )

            val patchedAccount = runBlocking { accountService.patchAccount(adminAccount, accountId1, toPatch) }

            assertAll("patchedAccount",
                { assertEquals(newLogin, patchedAccount.login) },
                { assertEquals(newEmail, patchedAccount.email) },
                { assertEquals(newPassword, patchedAccount.password) },
                { assertEquals(newFullName, patchedAccount.fullName) },
                { assertEquals(newCompany, patchedAccount.company) },
                { assertEquals(newCountry, patchedAccount.country) },
                { assertEquals(newPublicKey, patchedAccount.publicKey) },
                { assertEquals(newHash, patchedAccount.hash) },
                { assertEquals(newIsAdmin, patchedAccount.isAdmin) },
                { assertEquals(newDisabled, patchedAccount.disabled) }

            )
        }

        @Test
        fun patchNonexistentAccountTest() {
            val toPatch = AccountPatch(password = newPassword)

            assertThrows<EntityNotFoundException> {
                runBlocking {
                    accountService.patchAccount(
                        adminAccount,
                        accountNonexistentId,
                        toPatch
                    )
                }
            }
        }
    }

    @Nested
    inner class DeleteAccountTest {

        @Test
        fun deleteAccountTest() {
            runBlocking {
                accountService.deleteAccount(adminAccount, accountId1)

                assertEquals(
                    accountsInitialCount - 1,
                    mongoTemplate.getCollection("accounts").awaitSingle().countDocuments().awaitSingle()
                )
            }
        }

        @Test
        fun deleteNonexistentAccountTest() {
            assertThrows<EntityNotFoundException> { runBlocking { accountService.deleteAccount(adminAccount, accountNonexistentId) } }
        }
    }
}
