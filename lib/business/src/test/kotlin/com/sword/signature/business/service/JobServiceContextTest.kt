package com.sword.signature.business.service


import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Account
import com.sword.signature.business.model.Job
import com.sword.signature.business.model.JobPatch
import com.sword.signature.common.criteria.JobCriteria
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.model.migration.MigrationHandler
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.stream.Stream

class JobServiceContextTest @Autowired constructor(
    private val jobService: JobService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler: MigrationHandler
) : AbstractServiceContextTest() {


    private val simpleAccount = Account(
        id = "ljghdslkgjsdglhjdslghjdsklgjdgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = false,
        pubKey = null
    )

    private val adminAccount = Account(
        id = "5e74a073a386f170f3850b4b",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = true,
        pubKey = null
    )


    private val secondAdmin = Account(
        id = "ljghdslkgjsdglhgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        isAdmin = true,
        pubKey = null
    )
    private val multipleFileJobId = "5e8c36c49df469062bc658c1"
    private val singleFileJobId = "5e8c36c49df469062bc658c8"
    private val singleUserJob1Id = "5e8c36c49df469062bc658c9"
    private val singleUserJob2Id = "5e8c36c49df469062bc658d0"

    private val multipleFileJob = Job(
        id = multipleFileJobId,
        createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
        numberOfTry = 0,
        algorithm = "SHA-256",
        userId = "5e74a073a386f170f3850b4b",
        flowName = "ARS_20180626_02236_130006",
        stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
        state = JobStateType.INSERTED

    )

    private val singleFileJob = Job(
        id = singleFileJobId,
        createdDate = OffsetDateTime.parse("2020-04-08T08:16:04.114Z"),
        numberOfTry = 0,
        algorithm = "SHA-256",
        userId = "5e74a073a386f170f3850b4b",
        flowName = "ARS_20180626_02236_130006",
        stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.113Z"),
        state = JobStateType.INSERTED,
        channelName = "chaine editique"
    )


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDatasets(Path.of("src/test/resources/datasets/jobs.json"))
    }

    @Nested
    inner class FindAll {

        @Test
        fun `simple user can't list other's jobs `() {

            Assertions.assertThatThrownBy {
                runBlocking {
                    jobService.findAll(simpleAccount, JobCriteria(accountId = adminAccount.id)).toList()
                }
            }.isInstanceOf(IllegalAccessException::class.java)

        }

        @Test
        fun `simple user can't list all's jobs `() {

            Assertions.assertThatThrownBy {
                runBlocking {
                    jobService.findAll(simpleAccount).toList()
                }
            }.isInstanceOf(IllegalAccessException::class.java)

        }

        @Test
        fun `admin get his own jobs list`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(accountId = adminAccount.id)).map { it.id }.toList()

                val expected = listOf(multipleFileJobId, singleFileJobId)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `second admin get first's jobs list`() {
            runBlocking {


                val actual =
                    jobService.findAll(secondAdmin, JobCriteria(accountId = adminAccount.id)).map { it.id }.toList()

                val expected = listOf(multipleFileJobId, singleFileJobId)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `admin get all jobs list`() {
            runBlocking {

                val actual = jobService.findAll(adminAccount).map { it.id }.toList()

                val expected = listOf(multipleFileJobId, singleFileJobId, singleUserJob1Id, singleUserJob2Id)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `filter by id`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(id = "5e8c36c49df469062bc658d0")).map { it.id }.toList()

                val expected = listOf( singleUserJob2Id)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `filter by flowName`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(flowName = "rs_20180626_02236_13000")).map { it.id }
                        .toList()

                val expected = listOf(multipleFileJobId, singleFileJobId)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }


        @Test
        fun `filter by start date`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(dateStart = LocalDate.of(2020,4,8))).map { it.id }
                        .toList()

                val expected = listOf( singleFileJobId,singleUserJob1Id,singleUserJob2Id)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `filter by end date`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(dateEnd = LocalDate.of(2020,4,8))).map { it.id }
                        .toList()

                val expected = listOf(multipleFileJobId, singleFileJobId)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }
        @Test
        fun `filter by channel name`() {
            runBlocking {

                val actual =
                    jobService.findAll(adminAccount, JobCriteria(channelName = "ine edit")).map { it.id }
                        .toList()

                val expected = listOf( singleFileJobId,singleUserJob1Id)

                assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
            }
        }

        @Test
        fun `sort by channel name`() {
            runBlocking {
                val pageable = PageRequest.of(0, 30, Sort.Direction.ASC, "channelName")

                val actual =
                    jobService.findAll(requester=adminAccount, pageable=pageable).map { it.id }
                        .toList()

                val expected = listOf(multipleFileJobId,singleUserJob2Id, singleFileJobId,singleUserJob1Id)

                assertThat(actual).containsExactlyElementsOf(expected)
            }
        }

        @Test
        fun `sort by flowName`() {
            runBlocking {
                val pageable = PageRequest.of(0, 30, Sort.Direction.DESC, "flowName")
                val actual =
                    jobService.findAll(requester=adminAccount, pageable=pageable).map { it.id }
                        .toList()

                val expected = listOf(singleUserJob2Id,singleUserJob1Id,multipleFileJobId, singleFileJobId)
                assertThat(actual).containsExactlyElementsOf(expected)
            }
        }

        @Test
        fun `first page of two`() {
            runBlocking {
                val pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "flowName")
                val actual =
                    jobService.findAll(requester=adminAccount, pageable=pageable).map { it.id }
                        .toList()

                val expected = listOf(singleUserJob2Id,singleUserJob1Id)
                assertThat(actual).containsExactlyElementsOf(expected)
            }
        }

        @Test
        fun `second page of two`() {
            runBlocking {
                val pageable = PageRequest.of(1, 2, Sort.Direction.DESC, "flowName")
                val actual =
                    jobService.findAll(requester=adminAccount, pageable=pageable).map { it.id }
                        .toList()

                val expected = listOf(multipleFileJobId, singleFileJobId)
                assertThat(actual).containsExactlyElementsOf(expected)
            }
        }



    }

    @Nested
    inner class FindById {
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
            expectedFileNames: List<String>
        ) {
            runBlocking {
                val job = jobService.findById(requesterAccount, jobId, true)
                assertThat(job).isNotNull
                job as Job
                assertThat(job).`as`("mauvais job").isEqualToIgnoringGivenFields(expected, "rootHash", "files")
                assertThat(job.files?.map { it.metadata.fileName }).`as`("pas de file").isNotNull.`as`("mauvais files")
                    .containsExactlyInAnyOrderElementsOf(expectedFileNames)
            }
        }

        @ParameterizedTest
        @MethodSource("getSingleJobProvider")
        fun getSingleJobNoLeaf(
            requesterAccount: Account,
            jobId: String,
            expected: Job,
            expectedFileNames: List<String>
        ) {
            runBlocking {
                val job = jobService.findById(requesterAccount, jobId)
                assertThat(job).isNotNull
                job as Job
                assertThat(job).`as`("mauvais job").isEqualToIgnoringGivenFields(expected, "rootHash", "files")
                assertThat(job.files).`as`("pas de file").isNull()
            }
        }

        fun getSingleJobProvider(): Stream<Arguments> {
            return listOf(
                Arguments.of(
                    secondAdmin,
                    multipleFileJobId,
                    multipleFileJob,
                    listOf("ARS_02236_00004.pdf", "ARS_02236_00002.pdf", "ARS_02236_00001.pdf")
                )
                , Arguments.of(
                    secondAdmin,
                    singleFileJobId,
                    singleFileJob,
                    listOf("ARS_02236_00003.pdf")
                )
            ).stream()
        }
    }

    @Nested
    inner class Update {


        @Test
        fun `simple user can't update a job not his own`() {
            Assertions.assertThatThrownBy {
                runBlocking {
                    jobService.patch(simpleAccount, multipleFileJobId, JobPatch())
                }
            }.isInstanceOf(IllegalAccessException::class.java)
        }


        @Test
        fun `not existing job should thrown exception`() {
            Assertions.assertThatThrownBy {
                runBlocking {
                    jobService.patch(secondAdmin, "falseId", JobPatch())
                }
            }.isInstanceOf(EntityNotFoundException::class.java)
        }

        @Test
        fun `not existing job should thrown exception for everybody`() {
            Assertions.assertThatThrownBy {
                runBlocking {
                    jobService.patch(simpleAccount, "falseId", JobPatch())
                }
            }.isInstanceOf(EntityNotFoundException::class.java)
        }

        @ParameterizedTest
        @MethodSource("updateJobProvider")
        fun `working update`(
            comment: String,
            requester: Account,
            jobId: String,
            patch: JobPatch,
            expected: Job
        ) {
            runBlocking {
                val before = OffsetDateTime.now()
                val job = jobService.patch(requester, jobId, patch)

                assertThat(job).`as`(comment)
                    .isEqualToIgnoringGivenFields(expected, "injectedDate", "validatedDate", "stateDate")
                if (patch.state == JobStateType.INJECTED) {
                    assertThat(job.injectedDate).`as`("verification date injection").isAfter(before)
                }
                if (patch.state == JobStateType.VALIDATED) {
                    assertThat(job.validatedDate).`as`("verification date validation").isAfter(before)
                }
            }
        }


        fun updateJobProvider() = Stream.of(
            Arguments.of(
                "no change",
                secondAdmin,
                multipleFileJobId,
                JobPatch(),
                multipleFileJob
            ),
            Arguments.of(
                "numbreOfTry change",
                secondAdmin,
                multipleFileJobId,
                JobPatch(numberOfTry = 18),
                multipleFileJob.copy(numberOfTry = 18)
            ),
            Arguments.of(
                "blockId change",
                adminAccount,
                multipleFileJobId,
                JobPatch(blockHash = "superbeId"),
                multipleFileJob.copy(blockHash = "superbeId")
            ),
            Arguments.of(
                "blockDepth change",
                adminAccount,
                multipleFileJobId,
                JobPatch(blockDepth = 56),
                multipleFileJob.copy(blockDepth = 56)
            ),
            Arguments.of(
                "state change to INJECTED",
                adminAccount,
                multipleFileJobId,
                JobPatch(state = JobStateType.INJECTED),
                multipleFileJob.copy(state = JobStateType.INJECTED)
            ),
            Arguments.of(
                "state change to VALIDATED",
                adminAccount,
                multipleFileJobId,
                JobPatch(state = JobStateType.VALIDATED),
                multipleFileJob.copy(state = JobStateType.VALIDATED)
            )

        )


    }

}
