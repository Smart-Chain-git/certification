package com.sword.signature.business.service

import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.*
import com.sword.signature.common.enums.JobStateType
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.configuration.MongoConfiguration
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.util.stream.Stream


class SignServiceTest @Autowired constructor(
    override val mongoTemplate: ReactiveMongoTemplate,
    override val mongoConfiguration: MongoConfiguration,
    private val signService: SignService,
    private val jobRepository: JobRepository,
    private val nodeRepository: TreeElementRepository
) : AbstractServiceContextTest() {


    val accountAdmin = Account(
        id = "5e74a073a386f170f3850b4b",
        login = "admin",
        email = "admin@signature.com",
        password = "\$2a\$10\$TEQbC2lNT.dWnYVLOi8L4e5VUST7zyCV6demNJCQzNG6up3dr25Se",
        fullName = "Administrator",
        isAdmin = true
    )
    private final val sha256 = Algorithm(id = "SHA-256", name = "SHA-256", digestLength = 32)

    final val lorem1Hash = hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem1.txt")))
    final val lorem2Hash = hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem2.txt")))
    final val lorem3Hash = hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem3.txt")))
    final val lorem4Hash = hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem4.txt")))

    final val lorem1badHash = lorem1Hash + "kldsjgkldjs"

    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
    }

    @Nested
    inner class `Anchor files` {

        @Test
        fun `bad hash should reject`() {


            val monFlow = flow {
                emit(
                    Pair(second = FileMetadata(fileName = "lorem1Hash.pdf"), first = lorem1badHash)
                )
            }
            assertThatThrownBy {
                runBlocking {
                    signService.batchSign(
                        requester = accountAdmin,
                        algorithm = sha256,
                        flowName = "monflow",
                        fileHashs = monFlow
                    ).collect()
                }
            }.isInstanceOf(UserServiceException::class.java)


        }


        @ParameterizedTest
        @MethodSource("batchSignProvider")
        fun `batch Signature`(
            comments: String,
            account: Account,
            algorithm: String,
            hashs: List<Pair<String, FileMetadata>>,
            expectedJobsResponse: Int,
            expectedTreeElements: Int
        ) {
            runBlocking {

                val actualJobResponse =
                    signService.batchSign(
                        requester = account, algorithm = sha256,
                        flowName = "monflow", fileHashs = hashs.asFlow()
                    ).toList()

                assertThat(actualJobResponse).hasSize(expectedJobsResponse)


                //verification ecriture en bdd du job
                val jobs = jobRepository.findAll().asFlow().toList()
                assertThat(jobs).hasSize(expectedJobsResponse)
                val job = jobs[0]
                SoftAssertions().apply {
                    assertThat(job.userId).isEqualTo(account.id)
                    assertThat(job.algorithm).isEqualTo(algorithm)
                    assertThat(job.blockDepth).isNull()
                    assertThat(job.validatedDate).isNull()
                    assertThat(job.blockId).isNull()
                    assertThat(job.numbreOfTry).isEqualTo(0)
                }.assertAll()

                //verification de l'ecriture des feuille de l'arbre
                val nodes = nodeRepository.findAll().asFlow().toList()
                SoftAssertions().apply {
                    assertThat(nodes).describedAs("mauvais nombre de d'element crees").hasSize(expectedTreeElements)
                    assertThat(nodes.filter { it.parentId == null }).describedAs("il devrait y avoir $expectedJobsResponse racine")
                        .hasSize(expectedJobsResponse)
                    assertThat(nodes.filter { it.type == TreeElementType.LEAF }).describedAs("mauvais nombre de feuille cree")
                        .hasSize(hashs.size)
                }.assertAll()

            }
        }

        fun batchSignProvider(): Stream<Arguments> {
            val parametre = listOf(


                Arguments.of(
                    "single",
                    accountAdmin,
                    "SHA-256",
                    listOf(Pair(second = FileMetadata(fileName= "lorem1Hash.pdf"), first = lorem1Hash)),
                    1,
                    1
                ),
                Arguments.of(
                    "three",
                    accountAdmin,
                    "SHA-256",
                    listOf(
                        Pair(second = FileMetadata(fileName ="lorem2Hash.pdf"), first = lorem2Hash),
                        Pair(second = FileMetadata(fileName="lorem3Hash.pdf"), first = lorem3Hash),
                        Pair(second = FileMetadata(fileName="lorem4Hash.pdf"), first = lorem4Hash)
                    ),
                    1,
                    6
                ),
                Arguments.of(
                    "four",
                    accountAdmin,
                    "SHA-256",
                    listOf(
                        Pair(second = FileMetadata(fileName="lorem1Hash.pdf"), first = lorem1Hash),
                        Pair(second = FileMetadata(fileName="lorem2Hash.pdf"), first = lorem2Hash),
                        Pair(second = FileMetadata(fileName="lorem3Hash.pdf"), first = lorem3Hash),
                        Pair(second = FileMetadata(fileName="lorem4Hash.pdf"), first = lorem4Hash)
                    ),
                    2,
                    7
                )
            )

            return parametre.stream()
        }
    }

    @Nested
    inner class `Get file proof` {

        val file1Id = "5e8ed52fb1606a18565cbb95"
        val file2Id = "5e8ed52fb1606a18565cbb93"
        val file3Id = "5e8ed52fb1606a18565cbb97"
        val file4Id = "5e8ed52fb1606a18565cbb92"

        @BeforeEach
        fun refreshDatabase() {
            importJsonDataset(Path.of("src/test/resources/datasets/jobs.json"))
        }

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


        @Test
        fun `not existing fileproof should return null for autorised person`() {
            runBlocking {
                val job = signService.getFileProof(secondAdmin, "falseId")
                assertThat(job).isNull()
            }
        }

        @Test
        fun `not existing fileproof should return null for everybody`() {
            runBlocking {
                val job = signService.getFileProof(simpleAccount, "falseId")
                assertThat(job).isNull()
            }
        }


        @Test
        fun `user can't get proof of another`() {
            assertThatThrownBy {
                runBlocking {
                    signService.getFileProof(requester = simpleAccount, fileId = file1Id)
                }
            }.isInstanceOf(IllegalAccessException::class.java)
        }

        @ParameterizedTest
        @MethodSource("fileIdProvider")
        fun `get fileProof`(
            fileId: String,
            expectedProof: Pair<Job, List<TreeElement>>
        ) {
            runBlocking {
                val actual = signService.getFileProof(requester = accountAdmin, fileId = fileId)
                assertThat(actual).isNotNull.isEqualTo(expectedProof)
            }
        }

        private val multipleFileJobId = "5e8c36c49df469062bc658c1"
        private val singleFileJobId = "5e8c36c49df469062bc658c8"

        fun fileIdProvider(): Stream<Arguments> {
            return listOf(
                Arguments.of(
                    file1Id,
                    Pair(
                        Job(
                            id = multipleFileJobId,
                            createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            algorithm = "SHA-256",
                            userId = "5e74a073a386f170f3850b4b",
                            flowName = "ARS_20180626_02236_130006",
                            stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            state = JobStateType.INSERTED
                        ),
                        listOf(
                            TreeElement.LeafTreeElement(
                                id = "5e8ed52fb1606a18565cbb95",
                                hash = "145e9bccd897c6428d0e8b792fa3063646e435f10154df76aed7e0543daedcfc",
                                jobId = multipleFileJobId,
                                metadata = FileMetadata(fileName="ARS_02236_00001.pdf"),
                                position = TreeElementPosition.LEFT,
                                parentId = "5e8ed52fb1606a18565cbb94"

                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb94",
                                hash = "77062f8de93be903713c90bff1751949957a7c46aad3d584848b10c85ab63a60",
                                jobId = multipleFileJobId,
                                position = TreeElementPosition.RIGHT,
                                parentId = "5e8ed52fb1606a18565cbb90"
                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb90",
                                hash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                                jobId = multipleFileJobId
                            )
                        )
                    )
                ),
                Arguments.of(
                    file2Id,
                    Pair(
                        Job(
                            id = multipleFileJobId,
                            createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            algorithm = "SHA-256",
                            userId = "5e74a073a386f170f3850b4b",
                            flowName = "ARS_20180626_02236_130006",
                            stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            state = JobStateType.INSERTED
                        ),
                        listOf(
                            TreeElement.LeafTreeElement(
                                id = "5e8ed52fb1606a18565cbb93",
                                hash = "3d6d3491a321616e74c0db101da7b74205b09887079267b82ca76d19dd1d62ba",
                                jobId = multipleFileJobId,
                                metadata = FileMetadata(fileName="ARS_02236_00002.pdf"),
                                position = TreeElementPosition.RIGHT,
                                parentId = "5e8ed52fb1606a18565cbb91"

                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb91",
                                hash = "7a55f048ec7b92d8521761dae4b337dfe465170422df151a4a75eea88dd053b0",
                                jobId = multipleFileJobId,
                                position = TreeElementPosition.LEFT,
                                parentId = "5e8ed52fb1606a18565cbb90"
                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb90",
                                hash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                                jobId = multipleFileJobId
                            )
                        )
                    )
                ),
                Arguments.of(
                    file3Id,
                    Pair(
                        Job(
                            id = singleFileJobId,
                            createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.114Z"),
                            algorithm = "SHA-256",
                            userId = "5e74a073a386f170f3850b4b",
                            flowName = "ARS_20180626_02236_130006",
                            stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.113Z"),
                            state = JobStateType.INSERTED
                        ),
                        listOf(
                            TreeElement.LeafTreeElement(
                                id = "5e8ed52fb1606a18565cbb97",
                                hash = "7d70baefd8e6284346d5021dde2288a5aaf4ce03220ac7653ca3f697be4cf399",
                                jobId = singleFileJobId,
                                metadata = FileMetadata(fileName="ARS_02236_00003.pdf")
                            )
                        )
                    )
                )
                ,
                Arguments.of(
                    file4Id,
                    Pair(
                        Job(
                            id = multipleFileJobId,
                            createdDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            algorithm = "SHA-256",
                            userId = "5e74a073a386f170f3850b4b",
                            flowName = "ARS_20180626_02236_130006",
                            stateDate = OffsetDateTime.parse("2020-04-07T08:16:04.028Z"),
                            state = JobStateType.INSERTED
                        ),
                        listOf(
                            TreeElement.LeafTreeElement(
                                id = "5e8ed52fb1606a18565cbb92",
                                hash = "830c745f08bd19ecff04565dd7ff05ae92090a5e0624391478c6dafc0422d052",
                                jobId = multipleFileJobId,
                                metadata = FileMetadata(fileName="ARS_02236_00004.pdf"),
                                position = TreeElementPosition.LEFT,
                                parentId = "5e8ed52fb1606a18565cbb91"
                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb91",
                                hash = "7a55f048ec7b92d8521761dae4b337dfe465170422df151a4a75eea88dd053b0",
                                jobId = multipleFileJobId,
                                position = TreeElementPosition.LEFT,
                                parentId = "5e8ed52fb1606a18565cbb90"
                            ),
                            TreeElement.NodeTreeElement(
                                id = "5e8ed52fb1606a18565cbb90",
                                hash = "112602c26bb1329b1808ed4fb3737774d1422832b988fe5bf1c5196bc1ce5cf7",
                                jobId = multipleFileJobId
                            )
                        )
                    )
                )
            ).stream()
        }

    }


}