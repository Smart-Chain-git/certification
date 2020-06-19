package com.sword.signature.business.service

import com.ninjasquad.springmockk.MockkBean
import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.Proof
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.model.migration.MigrationHandler
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.SoftAssertions
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
        height = 3,
        bigMapIds = listOf(0)
    )

    @Nested
    inner class CheckDocumentWithoutProof {

        @Test
        fun checkDocumentTestKODocumentNotFound() {
            assertThrows<CheckException.HashNotPresent> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKOTreeError() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_error.json")
            )

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKOInsertedJob() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_inserted.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            assertThrows<CheckException.DocumentKnownUnknownRootHash> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKOInjectedJob() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_injected.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentKONoTransactionFound() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns null

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKODifferentContract() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated_different_contract.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKODifferentRootHash() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok_different_rootHash.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7b") }
            }
        }

        @Test
        fun checkDocumentTestKODifferentSigner() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated_different_signer.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKONoDepthFound() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns null

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestKOInsufficientDepth() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 25

            assertThrows<CheckException.IncoherentData> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestOKValidatedJob() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100
            coEvery { tezosReaderService.getContract("KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk") } returns contract

            val response =
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }

            SoftAssertions().apply {
                assertEquals(0, response.status)
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof.rootHash
                )
            }.assertAll()
        }
    }

    @Nested
    inner class CheckDocumentWithProof {

        private val proof = Proof(
            signatureDate = OffsetDateTime.parse("2020-06-03T14:49:35.142429Z"),
            filename = "PDF_5.pdf",
            rootHash = "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
            documentHash = "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
            hashes = listOf(
                Pair(null, TreeElementPosition.RIGHT),
                Pair(null, TreeElementPosition.RIGHT),
                Pair("804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01", TreeElementPosition.LEFT)
            ),
            algorithm = "SHA-256",
            contractAddress = "KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk",
            transactionHash = "ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi",
            blockHash = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23f",
            signerAddress = "tz1aSkwEot3L2kmUvcoxzjMomb9mvBNuzFK6",
            creatorAddress = "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb"
        )

        @Test
        fun checkDocumentTestKOMissingTransactionHash() {
            val incorrectProof = proof.copy(
                transactionHash = null
            )

            assertThrows<CheckException.IncorrectProofFile> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentDocumentHash() {
            assertThrows<CheckException.HashInconsistent> {
                runBlocking {
                    checkService.checkDocument(
                        "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                        proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentHashes() {
            val incorrectProof = proof.copy(
                hashes = listOf(
                    Pair("3a864f6e7f3da504bff732c5ec49763e0fb03053f17a1c3d2b8afbd5dd6041c4", TreeElementPosition.RIGHT),
                    Pair(null, TreeElementPosition.RIGHT),
                    Pair("804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01", TreeElementPosition.LEFT)
                )
            )
            assertThrows<CheckException.IncorrectRootHash> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKONoTransactionFound() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns null
            assertThrows<CheckException.NoTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentContractAddress() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            val incorrectProof = proof.copy(
                contractAddress = "Incorrect contract address"
            )
            assertThrows<CheckException.IncorrectTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentRootHash() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            val incorrectProof = proof.copy(
                documentHash = "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                rootHash = "3f42f4094ec0caac72ee42fbd9feca08a3b095123d41ccd15144c4455c3e0326"
            )
            assertThrows<CheckException.IncorrectTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                        incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentSignerAddress() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100

            val incorrectProof = proof.copy(
                signerAddress = "Incorrect signer address"
            )
            assertThrows<CheckException.IncorrectTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKONoDepthFound() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns null

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKOInsufficientDepth() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 25

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking {
                    checkService.checkDocument(
                        "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                        proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestOKNotInDatabase() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100
            coEvery { tezosReaderService.getContract("KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk") } returns contract


            val response = runBlocking {
                checkService.checkDocument(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    proof
                )
            }

            SoftAssertions().apply {
                assertEquals(2, response.status)
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof.rootHash
                )
            }.assertAll()
        }

        @Test
        fun checkDocumentTestOKInDatabase() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100
            coEvery { tezosReaderService.getContract("KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk") } returns contract


            val response = runBlocking {
                checkService.checkDocument(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    proof
                )
            }

            SoftAssertions().apply {
                assertEquals(1, response.status)
                assertEquals("tz1aSkwEot3L2kmUvcoxzjMomb9mvBNuzFK6" ,response.signer)
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof.rootHash
                )
            }.assertAll()
        }
    }


}