package com.sword.signature.business.service

import com.ninjasquad.springmockk.MockkBean
import com.sword.signature.business.exception.MissingRightException
import com.sword.signature.business.model.*
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.model.migration.MigrationHandler
import com.sword.signature.tezos.reader.service.TezosReaderService
import com.sword.signature.tezos.reader.tzindex.model.TzContract
import com.sword.signature.tezos.reader.tzindex.model.TzOp
import io.mockk.coEvery
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Path
import java.time.LocalDate
import java.time.OffsetDateTime

class FileServiceContextTest @Autowired constructor(
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler: MigrationHandler,
    private val fileService: FileService
) : AbstractServiceContextTest() {

    @MockkBean
    private lateinit var tezosReaderService: TezosReaderService

    val adminAccount = Account(
        id = "5e74a073a386f170f3850b4b",
        login = "admin",
        email = "admin@signature.com",
        password = "\$2a\$10\$TEQbC2lNT.dWnYVLOi8L4e5VUST7zyCV6demNJCQzNG6up3dr25Se",
        fullName = "Administrator",
        company = null,
        country = null,
        publicKey = null,
        hash = null,
        isAdmin = true,
        disabled = false,
        firstLogin = false
    )

    private val simpleAccount = Account(
        id = "ljghdslkgjsdglhjdslghjdsklgjdgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        company = null,
        country = null,
        publicKey = null,
        hash = null,
        isAdmin = false,
        disabled = false,
        firstLogin = false
    )

    private val secondAdmin = Account(
        id = "ljghdslkgjsdglhgskldjglgdsjgdlskgjdslknjcvhuire",
        login = "simple",
        password = "password",
        fullName = "simple user",
        email = "simplie@toto.net",
        company = null,
        country = null,
        publicKey = null,
        hash = null,
        isAdmin = true,
        disabled = false,
        firstLogin = false
    )

    private val job1Id = "5e8c36c49df469062bc658c1"
    private val job2Id = "5e8c36c49df469062bc658c8"
    private val job3Id = "5e8c36c49df469062bc658c9"
    private val job4Id = "5e8c36c49df469062bc658d0"
    private val job5Id = "5e8c36c49df469062bc658d6"
    private val job6Id = "5e8c36c49df469062bc658d7"

    private val file11Id = "5e8ed52fb1606a18565cbb92" // jobId: 5e8c36c49df469062bc658c1
    private val file12Id = "5e8ed52fb1606a18565cbb93" // jobId: 5e8c36c49df469062bc658c1
    private val file13Id = "5e8ed52fb1606a18565cbb95" // jobId: 5e8c36c49df469062bc658c1
    private val file2Id = "5e8ed52fb1606a18565cbb97" // jobId: 5e8c36c49df469062bc658c8
    private val file3Id = "5eea37705609738dde237658" // jobId: 5e8c36c49df469062bc658c9
    private val file4Id = "5eea37887e88a86920d0d1dc" // jobId: 5e8c36c49df469062bc658d0
    private val file5Id = "5eea37887e88a86920d0d1dd" // jobId: 5e8c36c49df469062bc658d6
    private val file6Id = "5eea37887e88a86920d0d1de" // jobId: 5e8c36c49df469062bc658d7

    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDatasets(Path.of("src/test/resources/datasets/jobs.json"))
    }

    @Nested
    inner class GetFileProof {
        private val blockchain = "TEZOS_TEST"
        private val webProviderURLNode = URLNode.fromWebProviderUrl(url ="WEB_PROVIDER_TEST")
        private val contractAddress = "KT1Tq22yXWayLbmBKwZUhVXMoYqxtzp9XvTk"
        private val contract = TzContract(
            address = contractAddress,
            manager = "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
            height = 3,
            bigMapIds = listOf(0)
        )

        @Test
        fun `not existing file proof should return null for authorized person`() {
            runBlocking {
                val fileProof = fileService.getFileProof(secondAdmin, "falseId").awaitFirstOrNull()
                assertThat(fileProof).isNull()
            }
        }

        @Test
        fun `not existing file proof should return null for everybody`() {
            runBlocking {
                val fileProof = fileService.getFileProof(simpleAccount, "falseId").awaitFirstOrNull()
                assertThat(fileProof).isNull()
            }
        }


        @Test
        fun `user can't get proof of another`() {
            Assertions.assertThatThrownBy {
                runBlocking {
                    fileService.getFileProof(requester = simpleAccount, fileId = file13Id).awaitFirstOrNull()
                }
            }.isInstanceOf(MissingRightException::class.java)
        }

        @ParameterizedTest
        @MethodSource("fileIdProvider")
        fun `get fileProof`(
            fileId: String,
            expectedProof: Proof
        ) {
            runBlocking {
                val actual = fileService.getFileProof(requester = adminAccount, fileId = fileId).awaitFirstOrNull()
                assertThat(actual).isNotNull.isEqualTo(expectedProof)
            }
        }

        @Test
        fun `get fileProof with validated job`() {
            val documentHash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf39c"
            val transactionHash = "opEUqHyA46QF7Jgr96DzqoZBiWekpg9swzyzJzDA4AZ9DvCWxcH"
            val apiStorageURLNode = URLNode(
                url ="http://127.0.0.1:8000/explorer/bigmap/"+contract.bigMapIds[0]+"/" + documentHash,
                type = URLNodeType.API_TEZOS_STORAGE,
                comment = "You can check the hash's anchorage in the tezos blockchain with this URL."
            )

            val apiTransactionURLNode = URLNode(
                url= "http://127.0.0.1:8000/explorer/op/$transactionHash",
                type = URLNodeType.API_TEZOS_TRANSACTION,
                comment = "You can check the transaction in the tezos blockchain with this URL."
            )
            val expectedProof = Proof(
                filename = "single_3.pdf",
                algorithm = "SHA-256",
                rootHash = documentHash,
                documentHash = documentHash,
                hashes = emptyList(),
                blockChain = blockchain,
                urls = listOf(apiStorageURLNode, apiTransactionURLNode, webProviderURLNode),
                contractAddress = contractAddress,
                transactionHash = "opEUqHyA46QF7Jgr96DzqoZBiWekpg9swzyzJzDA4AZ9DvCWxcH",
                signatureDate = OffsetDateTime.parse("2020-05-07T10:17:09.117Z"),
                creatorAddress = contract.manager
            )
            val actual = runBlocking {
                coEvery { tezosReaderService.getContract(contractAddress) } returns contract
                fileService.getFileProof(requester = adminAccount, fileId = file5Id).awaitFirstOrNull()
            }
            assertThat(actual).isNotNull.isEqualTo(expectedProof)
        }

        @Test
        fun `get fileProof with validated job and unset contractTimestamp`() {
            val documentHash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf39d"
            val transactionHash = "opEUqHyA46QF7Jgr96DzqoZBiWekpg9swzyzJzDA4AZ1DvCWxcH"
            val timestamp = OffsetDateTime.parse("2020-05-10T10:17:09.117Z")
            val transaction = TzOp(
                hash = "ooG3vVwQA51f6YiHd41wvomejuzkBWKJEgvGiYQ4zQK4jrXBFCi",
                block = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23f",
                time = OffsetDateTime.parse("2020-06-03T14:50:04Z"),
                height = 15,
                bigMapDiff = listOf(
                    TzOp.BigMapDiff(
                        key = "10cba66df788a0848e397c396b993057c64bb29cadc78152246ad28c1a3b02ef",
                        keyHash = "exprukefU3KUcVMbUCVa9nh3TkRfdPiVLHYN3ehxorRsT6hYpcYFvM",
                        value = TzOp.BigMapDiff.Value(
                            timestamp = timestamp,
                            address = "tz1aSkwEot3L2kmUvcoxzjMomb9mvBNuzFK6"
                        ),
                        meta = TzOp.BigMapDiff.Meta(
                            contract = contractAddress,
                            bigmapId = contract.bigMapIds[0],
                            time = OffsetDateTime.parse("2020-06-03T14:50:04Z"),
                            height = 15,
                            block = "BMeaiFq5S6EuPVR3ctvNGWT7dufc35SjNkHyiKFbTKiC22DH23f"
                        ),
                        action = "update"
                    )
                )
            )
            val apiStorageURLNode = URLNode(
                url ="http://127.0.0.1:8000/explorer/bigmap/"+contract.bigMapIds[0]+"/" + documentHash,
                type = URLNodeType.API_TEZOS_STORAGE,
                comment = "You can check the hash's anchorage in the tezos blockchain with this URL."
            )

            val apiTransactionURLNode = URLNode(
                url= "http://127.0.0.1:8000/explorer/op/$transactionHash",
                type = URLNodeType.API_TEZOS_TRANSACTION,
                comment = "You can check the transaction in the tezos blockchain with this URL."
            )
            val expectedProof = Proof(
                filename = "single_4.pdf",
                algorithm = "SHA-256",
                rootHash = documentHash,
                documentHash = documentHash,
                hashes = emptyList(),
                blockChain = blockchain,
                urls = listOf(apiStorageURLNode, apiTransactionURLNode, webProviderURLNode),
                contractAddress = contractAddress,
                transactionHash = "opEUqHyA46QF7Jgr96DzqoZBiWekpg9swzyzJzDA4AZ1DvCWxcH",
                signatureDate = timestamp,
                creatorAddress = contract.manager
            )
            val actual = runBlocking {
                coEvery { tezosReaderService.getContract(contractAddress) } returns contract
                coEvery { tezosReaderService.getTransaction(transactionHash) } returns transaction
                fileService.getFileProof(requester = adminAccount, fileId = file6Id).awaitFirstOrNull()
            }
            assertThat(actual).isNotNull.isEqualTo(expectedProof)
        }

        private val multipleFileJobId = "5e8c36c49df469062bc658c1"
        private val singleFileJobId = "5e8c36c49df469062bc658c8"

        fun fileIdProvider() = listOf(
            Arguments.of(
                file13Id,
                Proof(
                    filename = "ARS_02236_00001.pdf",
                    algorithm = "SHA-256",
                    rootHash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                    documentHash = "145e9bccd897c6428d0e8b792fa3063646e435f10154df76aed7e0543daedcfc",
                    hashes = listOf(
                        Pair(null, TreeElementPosition.RIGHT),
                        Pair(
                            "7a55f048ec7b92d8521761dae4b337dfe465170422df151a4a75eea88dd053b0",
                            TreeElementPosition.LEFT
                        )
                    ),
                    blockChain = blockchain,
                    urls = listOf(webProviderURLNode)
                )
            ),
            Arguments.of(
                file12Id,
                Proof(
                    filename = "ARS_02236_00002.pdf",
                    algorithm = "SHA-256",
                    rootHash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                    documentHash = "3d6d3491a321616e74c0db101da7b74205b09887079267b82ca76d19dd1d62ba",
                    hashes = listOf(
                        Pair(
                            "830c745f08bd19ecff04565dd7ff05ae92090a5e0624391478c6dafc0422d052",
                            TreeElementPosition.LEFT
                        ),
                        Pair(
                            "77062f8de93be903713c90bff1751949957a7c46aad3d584848b10c85ab63a60",
                            TreeElementPosition.RIGHT
                        )
                    ),
                    blockChain = blockchain,
                    urls = listOf(webProviderURLNode)
                )
            ),
            Arguments.of(
                file2Id,
                Proof(
                    filename = "ARS_02236_00003.pdf",
                    algorithm = "SHA-256",
                    rootHash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf399",
                    documentHash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf399",
                    hashes = emptyList(),
                    blockChain = blockchain,
                    urls = listOf(webProviderURLNode)
                )
            )
            ,
            Arguments.of(
                file11Id,
                Proof(
                    filename = "ARS_02236_00004.pdf",
                    algorithm = "SHA-256",
                    rootHash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                    documentHash = "830c745f08bd19ecff04565dd7ff05ae92090a5e0624391478c6dafc0422d052",
                    hashes = listOf(
                        Pair(
                            "3d6d3491a321616e74c0db101da7b74205b09887079267b82ca76d19dd1d62ba",
                            TreeElementPosition.RIGHT
                        ),
                        Pair(
                            "77062f8de93be903713c90bff1751949957a7c46aad3d584848b10c85ab63a60",
                            TreeElementPosition.RIGHT
                        )
                    ),
                    blockChain = blockchain,
                    urls = listOf(webProviderURLNode)
                )
            )
        )
    }

    @Nested
    inner class GetFiles {

        @Test
        fun `simple user can not get all files`() {
            assertThrows<MissingRightException> {
                runBlocking {
                    fileService.getFiles(
                        requester = simpleAccount
                    )
                }
            }
        }

        @Test
        fun `simple user can not get others' files`() {
            assertThrows<MissingRightException> {
                runBlocking {
                    fileService.getFiles(
                        requester = simpleAccount,
                        filter = FileFilter(accountId = adminAccount.id)
                    )
                }
            }
        }

        @Test
        fun `simple user can get its own files`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = simpleAccount,
                        filter = FileFilter(accountId = simpleAccount.id)
                    ).toList()
                }
            val expected = listOf(file3Id, file4Id, file5Id, file6Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `admin user can get all files`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount
                    ).toList()
                }
            val expected = listOf(file11Id, file12Id, file13Id, file2Id, file3Id, file4Id, file5Id, file6Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `admin user can get its own files`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(accountId = adminAccount.id)
                    ).toList()
                }
            val expected = listOf(file11Id, file12Id, file13Id, file2Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `admin user can get simple account's files`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(accountId = simpleAccount.id)
                    ).toList()
                }
            val expected = listOf(file3Id, file4Id, file5Id, file6Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `second admin can get first admin's files`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = secondAdmin,
                        filter = FileFilter(accountId = adminAccount.id)
                    ).toList()
                }
            val expected = listOf(file11Id, file12Id, file13Id, file2Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by file id`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(id = file13Id)
                    ).toList()
                }
            val expected = listOf(file13Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by file name`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(name = "Single")
                    ).toList()
                }
            val expected = listOf(file3Id, file4Id, file5Id, file6Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by file hash`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(hash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf39b")
                    ).toList()
                }
            val expected = listOf(file4Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by job id`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(jobId = "5e8c36c49df469062bc658c1")
                    ).toList()
                }
            val expected = listOf(file11Id, file12Id, file13Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by job start date`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(dateStart = LocalDate.of(2020, 4, 8))
                    ).toList()
                }
            val expected = listOf(file2Id, file3Id, file4Id, file5Id, file6Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }

        @Test
        fun `filter by job end date`() {
            val files =
                runBlocking {
                    fileService.getFiles(
                        requester = adminAccount,
                        filter = FileFilter(dateEnd = LocalDate.of(2020, 4, 8))
                    ).toList()
                }
            val expected = listOf(file11Id, file12Id, file13Id, file2Id)
            assertEquals(expected.size, files.size)
            assertThat(files.map { it.id }).containsExactlyInAnyOrderElementsOf(expected)
        }
    }
}
