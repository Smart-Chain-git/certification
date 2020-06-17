package com.sword.signature.business.service

import com.sword.signature.business.exception.UserServiceException
import com.sword.signature.business.model.*
import com.sword.signature.common.enums.TreeElementPosition
import com.sword.signature.common.enums.TreeElementType
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.migration.MigrationHandler
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
import java.util.stream.Stream


class SignServiceTest @Autowired constructor(
    override val mongoTemplate: ReactiveMongoTemplate,
    override val migrationHandler: MigrationHandler,
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
        isAdmin = true,
        pubKey = null
    )
    private final val sha256 = Algorithm(id = "SHA-256", name = "SHA-256", digestLength = 32)

    final val lorem1Hash =
        hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem1.txt")))
    final val lorem2Hash =
        hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem2.txt")))
    final val lorem3Hash =
        hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem3.txt")))
    final val lorem4Hash =
        hexStringHash(sha256.name, Files.readAllBytes(Paths.get("src/test/resources/files/lorem4.txt")))

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
                        channelName = "testChannel",
                        algorithm = sha256,
                        flowName = "monflow",
                        fileHashes = monFlow
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
                        requester = account, channelName = "testChannel", algorithm = sha256,
                        flowName = "monflow", fileHashes = hashs.asFlow()
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
                    assertThat(job.blockHash).isNull()
                    assertThat(job.numberOfTry).isEqualTo(0)
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
                    listOf(Pair(second = FileMetadata(fileName = "lorem1Hash.pdf"), first = lorem1Hash)),
                    1,
                    1
                ),
                Arguments.of(
                    "three",
                    accountAdmin,
                    "SHA-256",
                    listOf(
                        Pair(second = FileMetadata(fileName = "lorem2Hash.pdf"), first = lorem2Hash),
                        Pair(second = FileMetadata(fileName = "lorem3Hash.pdf"), first = lorem3Hash),
                        Pair(second = FileMetadata(fileName = "lorem4Hash.pdf"), first = lorem4Hash)
                    ),
                    1,
                    6
                ),
                Arguments.of(
                    "four",
                    accountAdmin,
                    "SHA-256",
                    listOf(
                        Pair(second = FileMetadata(fileName = "lorem1Hash.pdf"), first = lorem1Hash),
                        Pair(second = FileMetadata(fileName = "lorem2Hash.pdf"), first = lorem2Hash),
                        Pair(second = FileMetadata(fileName = "lorem3Hash.pdf"), first = lorem3Hash),
                        Pair(second = FileMetadata(fileName = "lorem4Hash.pdf"), first = lorem4Hash)
                    ),
                    2,
                    7
                )
            )

            return parametre.stream()
        }
    }

}
