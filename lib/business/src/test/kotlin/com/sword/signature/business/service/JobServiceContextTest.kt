package com.sword.signature.business.service

import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.model.configuration.MongoConfiguration

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.OffsetDateTime
import java.util.stream.Stream

class JobServiceContextTest @Autowired constructor(
    private val jobService: JobService,
    private val accountService: AccountService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {


    private val simpleAccount = Account(
        id = "ljghdslkgjsdglhjdslghjdsklgjdgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = false
    )

    private val secondAdmin = Account(
        id = "ljghdslkgjsdglhgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = true
    )
    private val multipleFileJobId = "5e8c36c49df469062bc658c1"
    private val singleFileJobId = "5e8c36c49df469062bc658c8"


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/jobs.json"))
    }

    @Test
    fun `simple user can't list other's jobs `() {

        Assertions.assertThatThrownBy {
            runBlocking {
                val adminAccount =
                    accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")
                jobService.findAllByUser(simpleAccount, adminAccount).toList()
            }
        }.isInstanceOf(IllegalAccessException::class.java)

    }

    @Test
    fun `admin get his own jobs list`() {
        runBlocking {
            val adminAccount =
                accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")

            val jobs = jobService.findAllByUser(adminAccount, adminAccount).toList()

            val expected = listOf(
                Job(
                    id = "5e8c36c49df469062bc658c1",
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    state = JobStateType.INSERTED
                )
            )

            assertThat(jobs).hasSize(2).containsAnyElementsOf(expected)
        }
    }

    @Test
    fun `second admin get first's jobs list`() {
        runBlocking {
            val adminAccount =
                accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")

            val jobs = jobService.findAllByUser(secondAdmin, adminAccount).toList()

            val expected = listOf(
                Job(
                    id = multipleFileJobId,
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    state = JobStateType.INSERTED
                )
            )

            assertThat(jobs).hasSize(2).containsAnyElementsOf(expected)
        }
    }


    @Test
    fun `simple user can't have a job not his own`() {
        Assertions.assertThatThrownBy {
            runBlocking {
                jobService.findById(simpleAccount, multipleFileJobId)
            }
        }.isInstanceOf(IllegalAccessException::class.java)
    }


    @Test
    fun `not existing job should return null for autorised person`() {
        runBlocking {
            val job = jobService.findById(secondAdmin, "falseId")
            assertThat(job).isNull()
        }
    }

    @Test
    fun `not existing job should return null for everybody`() {
        runBlocking {
            val job = jobService.findById(simpleAccount, "falseId")
            assertThat(job).isNull()
        }
    }

    @ParameterizedTest
    @MethodSource("getSingleJobProvider")
    fun getSingleJob(
        requesterAccount: Account,
        jobId: String,
        expected: Job,
    expectedFileNames : List<String>
    ) {
        runBlocking {
            val job = jobService.findById(requesterAccount, jobId,true)
            assertThat(job).isNotNull
            job as Job
            assertThat(job).`as`("mauvais job").isEqualToIgnoringGivenFields(expected, "files")
            assertThat(job.files?.map { it.fileName }).`as`("pas de file").isNotNull.`as`("mauvais files")
                .containsExactlyInAnyOrderElementsOf(expectedFileNames)
        }
    }

    @ParameterizedTest
    @MethodSource("getSingleJobProvider")
    fun getSingleJobNoLeaf(
        requesterAccount: Account,
        jobId: String,
        expected: Job,
        expectedFileNames : List<String>
    ) {
        runBlocking {
            val job = jobService.findById(requesterAccount, jobId)
            assertThat(job).isNotNull
            job as Job
            assertThat(job).`as`("mauvais job").isEqualToIgnoringGivenFields(expected, "files")
            assertThat(job.files).`as`("pas de file").isNull()
        }
    }

    fun getSingleJobProvider(): Stream<Arguments> {
        return listOf(
            Arguments.of(
                secondAdmin,
                multipleFileJobId,
                Job(
                    id = multipleFileJobId,
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                    state = JobStateType.INSERTED
                ),
                listOf("ARS_02236_00004.pdf", "ARS_02236_00002.pdf", "ARS_02236_00001.pdf")
            )
            , Arguments.of(
                secondAdmin,
                singleFileJobId,
                Job(
                    id = singleFileJobId,
                    createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.114Z"),
                    numbreOfTry = 0,
                    algorithm = "SHA-256",
                    userId = "5e74a073a386f170f3850b4b",
                    flowName = "ARS_20180626_02236_130006",
                    stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.113Z"),
                    state = JobStateType.INSERTED
                ),
                listOf("ARS_02236_00003.pdf")
            )
        ).stream()


    }

}
