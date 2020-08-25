package com.sword.signature.business.service

import com.ninjasquad.springmockk.MockkBean
import com.sword.signature.business.exception.CheckException
import com.sword.signature.business.model.Proof
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.model.migration.MigrationHandler
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzBigMapEntry
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

    private val adminFullName = "Administrator"
    private val fileId = "5ed7b83b3a635b44c7f8e952"
    private val jobId = "5ed7b83b3a635b44c7f8e946"

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
                    timestamp = OffsetDateTime.parse("2020-06-03T14:48:35.142429Z"),
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
            coEvery {
                tezosReaderService.getHashFromContract(
                    any(),
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a"
                )
            } returns null
            assertThrows<CheckException.HashNotPresent> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }
            }
        }

        @Test
        fun checkDocumentTestOKDocumentNotFoundButInChaine() {
            coEvery {
                tezosReaderService.getHashFromContract(
                    any(),
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7b"
                )
            } returns TzBigMapEntry(
                key = "clef??",
                keyHash = "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7b",
                value = TzBigMapEntry.Value(
                    timestamp = OffsetDateTime.now().minusSeconds(5),
                    address = "adresss"
                ),
                meta = TzBigMapEntry.Meta(
                    contract = "46546516519568465165",
                    bigMapId = 1,
                    block = "bli1114",
                    height = 14,
                    time = OffsetDateTime.now().minusSeconds(5)
                )

            )
            assertThrows<CheckException.TransactionNotOldEnough> {
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7b") }
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
        fun checkDocumentTestKORejectedJob() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_rejected.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            assertThrows<CheckException.HashNotPresent> {
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

            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            val response =
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }

            SoftAssertions().apply {
                assertEquals(1, response.status)
                assertEquals(fileId, response.fileId)
                assertEquals(jobId, response.jobId)
                assertEquals(adminFullName, response.signer)
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof?.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof?.rootHash
                )
            }.assertAll()
        }

        @Test
        fun checkDocumentTestOKValidatedJobWithDuplicate() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated_duplicate.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok_duplicate.json")
            )

            val otherTransaction = transaction.copy(
                hash = "ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCa",
                block = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23a",
                bigMapDiff = listOf(transaction.bigMapDiff[0].copy(key = "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a"))
            )

            coEvery { tezosReaderService.getTransaction(otherTransaction.hash) } returns otherTransaction
            coEvery { tezosReaderService.getTransactionDepth(otherTransaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            val response =
                runBlocking { checkService.checkDocument("c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a") }

            SoftAssertions().apply {
                assertEquals(1, response.status)
                assertEquals("5ed7b83b3a635b44c7f8e996", response.fileId)
                assertEquals("5ed7b83b3a635b44c7f8e944", response.jobId)
                assertEquals(adminFullName, response.signer)
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof?.documentHash
                )
                assertEquals(
                    "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a",
                    response.proof?.rootHash
                )
            }.assertAll()
        }
    }

    @Nested
    inner class CheckDocumentWithProof {

        private val documentHash = "c866779f483855455631c934d8933bf744f56dcc10833e8a73752ed086325a7a"
        private val proof = Proof(
            signatureDate = OffsetDateTime.parse("2020-06-03T14:48:35.142429Z"),
            filename = "PDF_5.pdf",
            rootHash = "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
            documentHash = documentHash,
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
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentContractFromConfiguration() {
            val incorrectProof = proof.copy(
                contractAddress = "valid address but different from configuration"
            )

            assertThrows<CheckException.IncorrectContractAddress> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentDocumentHash() {
            assertThrows<CheckException.IncorrectHash> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                        providedProof = proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKOUnknownAlgorithm() {
            val incorrectProof = proof.copy(
                algorithm = "Unknown algorithm"
            )

            assertThrows<CheckException.UnknownHashAlgorithm> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKOHashDifferentAlgorithm() {
            val incorrectProof = proof.copy(
                algorithm = "SHA-512"
            )

            assertThrows<CheckException.IncorrectHashAlgorithm> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
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
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKONoTransactionFound() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns null
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract
            assertThrows<CheckException.NoTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentContractAddress() {
            val incorrectProof = proof.copy(
                contractAddress = "Incorrect contract address"
            )

            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(incorrectProof.contractAddress!!) } returns contract

            assertThrows<CheckException.IncorrectContractAddress> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentSignerAddress() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            val incorrectProof = proof.copy(
                signerAddress = "Incorrect signer address"
            )
            assertThrows<CheckException.IncorrectPublicKey> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentSignatureDate() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            val incorrectProof = proof.copy(
                signatureDate = OffsetDateTime.parse("2020-06-03T14:49:35.142429Z")
            )
            assertThrows<CheckException.IncorrectSignatureDate> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentRootHash() {
            coEvery { tezosReaderService.getTransaction("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns transaction
            coEvery { tezosReaderService.getTransactionDepth("ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi") } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            val incorrectProof = proof.copy(
                documentHash = "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                rootHash = "3f42f4094ec0caac72ee42fbd9feca08a3b095123d41ccd15144c4455c3e0326"
            )
            assertThrows<CheckException.IncorrectTransaction> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = "804f3c79ae5897a66c9545fac06c27421118ae8cfa17806269bac736f374bd01",
                        providedProof = incorrectProof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKODifferentContractManager() {
            val contractWithDifferentManager = contract.copy(
                manager = "Different manager address"
            )

            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contractWithDifferentManager.address) } returns contractWithDifferentManager

            assertThrows<CheckException.IncoherentOriginPublicKey> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = proof
                    )
                }
            }
        }


        @Test
        fun checkDocumentTestKONoDepthFound() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns null

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestKOInsufficientDepth() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 25
            coEvery { tezosReaderService.getContract(contract.address) } returns contract

            assertThrows<CheckException.TransactionNotDeepEnough> {
                runBlocking {
                    checkService.checkDocument(
                        documentHash = documentHash,
                        providedProof = proof
                    )
                }
            }
        }

        @Test
        fun checkDocumentTestOKNotInDatabase() {
            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract


            val response = runBlocking {
                checkService.checkDocument(
                    documentHash = documentHash,
                    providedProof = proof
                )
            }

            SoftAssertions().apply {
                assertEquals(2, response.status)
                assertEquals(
                    documentHash,
                    response.proof?.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof?.rootHash
                )
            }.assertAll()
        }

        @Test
        fun checkDocumentTestOKInDatabase() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok.json")
            )

            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract


            val response = runBlocking {
                checkService.checkDocument(
                    documentHash = documentHash,
                    providedProof = proof
                )
            }

            SoftAssertions().apply {
                assertEquals(1, response.status)
                assertEquals(fileId, response.fileId)
                assertEquals(jobId, response.jobId)
                assertEquals(adminFullName, response.signer)
                assertEquals(
                    documentHash,
                    response.proof?.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof?.rootHash
                )
            }.assertAll()
        }

        @Test
        fun checkDocumentTestOKInDatabaseWithDuplicate() {
            importJsonDatasets(
                Path.of("src/test/resources/datasets/check/jobs_validated_duplicate.json"),
                Path.of("src/test/resources/datasets/check/treeElements_ok_duplicate.json")
            )

            coEvery { tezosReaderService.getTransaction(transaction.hash) } returns transaction
            coEvery { tezosReaderService.getTransactionDepth(transaction.hash) } returns 100
            coEvery { tezosReaderService.getContract(contract.address) } returns contract


            val response = runBlocking {
                checkService.checkDocument(
                    documentHash = documentHash,
                    providedProof = proof
                )
            }

            SoftAssertions().apply {
                assertEquals(1, response.status)
                assertEquals(fileId, response.fileId)
                assertEquals(jobId, response.jobId)
                assertEquals(adminFullName, response.signer)
                assertEquals(
                    documentHash,
                    response.proof?.documentHash
                )
                assertEquals(
                    "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                    response.proof?.rootHash
                )
            }.assertAll()
        }
    }
}
