package com.sword.signature.business.service

import com.ninjasquad.springmockk.MockkBean
import com.sword.signature.business.exception.CheckException
import com.sword.signature.model.migration.MigrationHandler
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Path
import java.time.OffsetDateTime

@ExtendWith(SpringExtension::class)
class CheckServiceContextTest @Autowired constructor(
    private val checkService: CheckService,
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler: MigrationHandler
) : AbstractServiceContextTest() {

    @MockkBean
    private lateinit var tezosReaderService: TezosReaderService

    @BeforeEach
    fun initDatabase() {
        resetDatabase()
    }

    private val transaction = TzOp(
        hash = "ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi",
        block = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23f",
        time = OffsetDateTime.parse("2020-06-03T14:50:04Z"),
        height = 15,
        bigMapDiff = listOf(
            TzOp.BigMapDiff(
                key = "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                keyHash = "exprukefU3KUcVMbUCVa9nh3TkRfdPiVLHYN3ehxorRsT6hYpcYFvM",
                value = TzOp.BigMapDiff.Value(
                    timestamp = OffsetDateTime.parse("2020-06-03T14:49:44Z"),
                    address = "tz1aSkwEot3L2kmUvcoxzjMomb9mvBNuzFK6"
                ),
                meta = TzOp.BigMapDiff.Meta(
                    contract = "KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk",
                    bigmapId = 0,
                    time = OffsetDateTime.parse("2020-06-03T14:50:04Z"),
                    height = 15,
                    block = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23f"
                ),
                action = "update"
            )
        )
    )

    private val contract = TzContract(
        address = "KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk",
        manager = "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
        height = 3
    )

    @Nested
    inner class CheckDocumentWithoutProof {
        @Test
        fun checkDocumentTestValidatedJob() {
            importJsonDataset(Path.of("src/test/resources/datasets/check/check_validated.json"))

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100
            coEvery { tezosReaderService.getContract("KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk") } returns contract

            val response =
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }

            assertEquals("In Progress", response.status)
        }

        @Test
        fun checkDocumentTestInsertedJob() {
            importJsonDataset(Path.of("src/test/resources/datasets/check/check_inserted.json"))

            assertThrows<CheckException.DocumentKnownUnknownRootHash> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestInjectedJob() {
            importJsonDataset(Path.of("src/test/resources/datasets/check/check_injected.json"))

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestTreeError() {
            importJsonDataset(Path.of("src/test/resources/datasets/check/check_treeError.json"))

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }
    }


}