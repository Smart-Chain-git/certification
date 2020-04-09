package com.sword.signature.business.service;

import com.sword.signature.business.exception.AccountNotFoundException
import com.sword.signature.business.exception.AlgorithmNotFoundException
import com.sword.signature.business.exception.EntityNotFoundException
import com.sword.signature.business.model.Job
import com.sword.signature.model.configuration.MongoConfiguration
import com.sword.signature.model.entity.JobState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.OffsetTime

class JobServiceContextTest @Autowired constructor(
    private val jobService: JobService,
    private val accountService: AccountService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val mongoConfiguration: MongoConfiguration
) : AbstractServiceContextTest() {


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/jobs.json"))
    }


    @Test
    fun `get Jobs for admin`() {
        runBlocking {
            val adminAccount =
                accountService.getAccountByLoginOrEmail("admin") ?: throw AccountNotFoundException("admin")

            val accounts = jobService.findAllByUser(adminAccount).toList()

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
