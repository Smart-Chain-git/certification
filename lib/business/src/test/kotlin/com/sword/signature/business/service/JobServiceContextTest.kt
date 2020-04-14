package com.sword.signature.business.service

import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.model.configuration.MongoConfiguration
import com.sword.signature.model.entity.JobState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.OffsetDateTime

class JobServiceContextTest @Autowired constructor(
    private val jobService: JobService,
    private val accountService: AccountService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {


    val simpleAccount = Account(
        id = "ljghdslkgjsdglhjdslghjdsklgjdgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = false
    )

    val secondAdmin = Account(
        id = "ljghdslkgjsdglhgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = true
    )


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/jobs.json"))
    }

    @Test
    fun `simple user can't list otheer's jobs `() {

        Assertions.assertThatThrownBy {
            runBlocking {
                val adminAccount =
                    accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")
                jobService.findAllByUser(simpleAccount, adminAccount).toList()
            }
        }.isInstanceOf(IllegalAccessException::class.java)

    }

    @Test
    fun `admin get his own jobs`() {
        runBlocking {
            val adminAccount =
                accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")

            val accounts = jobService.findAllByUser(adminAccount, adminAccount).toList()

            val expected = listOf(
                Job(
                    id = "5e8c36c49df469062bc658c1",
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    state = JobState.INSERTED
                )
            )

            assertThat(accounts).hasSize(2).containsAnyElementsOf(expected)
        }
    }

    @Test
    fun `second admin get first's jobs`() {
        runBlocking {
            val adminAccount =
                accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")

            val accounts = jobService.findAllByUser(secondAdmin, adminAccount).toList()

            val expected = listOf(
                Job(
                    id = "5e8c36c49df469062bc658c1",
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    state = JobState.INSERTED
                )
            )

            assertThat(accounts).hasSize(2).containsAnyElementsOf(expected)
        }
    }


}
