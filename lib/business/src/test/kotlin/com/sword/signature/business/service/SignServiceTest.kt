package com.sword.signature.business.service

import com.sword.signature.business.model.Account
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.entity.TreeElementType
import com.sword.signature.model.repository.JobRepository
import com.sword.signature.model.repository.TreeElementRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Stream


class SignServiceTest @Autowired constructor(
    override val mongoTemplate: ReactiveMongoTemplate,
    private val signService: SignService,
    private val jobRepository: JobRepository,
    private val nodeRepository: TreeElementRepository
) : AbstractServiceContextTest() {


    val accountAdmin = Account(
        id = "5e734ba4b075db359ea73a68",
        login = "admin",
        email = "account1@signature.com",
        password = "password",
        fullName = "super Admin"
    )


    val lorem1Hash = hexStringHash("SHA-256", Files.readAllBytes(Paths.get("src/test/resources/files/lorem1.txt")))
    val lorem2Hash = hexStringHash("SHA-256", Files.readAllBytes(Paths.get("src/test/resources/files/lorem2.txt")))
    val lorem3Hash = hexStringHash("SHA-256", Files.readAllBytes(Paths.get("src/test/resources/files/lorem3.txt")))
    val lorem4Hash = hexStringHash("SHA-256", Files.readAllBytes(Paths.get("src/test/resources/files/lorem4.txt")))


    @BeforeEach
    fun refreshDatabase() {
        resetDatabase()
        importJsonDataset(Path.of("src/test/resources/datasets/signs.json"))
    }


    @ParameterizedTest
    @MethodSource("batchSignProvider")
    fun `batch Signature`(
        comments: String,
        account: Account,
        algorithm: String,
        hashs: List<Pair<String,String>>,
        expectedJobsResponse: Int,
        expectedTreeElements: Int
    ) {
        runBlocking {

            val actualJobResponse = signService.batchSign(account = account,algorithm = algorithm, fileHashs = hashs.asFlow()).toList()

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
                assertThat(nodes.filter { it.parentId == null }).describedAs("il devrait y avoir $expectedJobsResponse racine").hasSize(expectedJobsResponse)
                assertThat(nodes.filter { it.type == TreeElementType.LEAF }).describedAs("mauvais nombre de feuille cree")
                    .hasSize(hashs.size)
            }.assertAll()

        }
    }

    fun batchSignProvider(): Stream<Arguments> {
        val parametre = ArrayList<Arguments>()

        parametre.add(
            Arguments.of(
                "single",
                accountAdmin,
                "SHA-256",
                listOf(Pair(second = "lorem1Hash.pdf", first = lorem1Hash )),
                1,
                1
            )
        )

        parametre.add(
            Arguments.of(
                "three",
                accountAdmin,
                "SHA-256",
                listOf(
                    Pair(second = "lorem2Hash.pdf", first = lorem2Hash),
                    Pair(second = "lorem3Hash.pdf", first = lorem3Hash),
                    Pair(second = "lorem4Hash.pdf", first = lorem4Hash)
                ),
                1,
                6
            )
        )


        parametre.add(
            Arguments.of(
                "three",
                accountAdmin,
                "SHA-256",
                listOf(
                    Pair(second = "lorem1Hash.pdf", first = lorem1Hash ),
                    Pair(second = "lorem2Hash.pdf", first = lorem2Hash ),
                    Pair(second = "lorem3Hash.pdf", first = lorem3Hash ),
                    Pair(second = "lorem4Hash.pdf", first = lorem4Hash )
                ),
                2,
                7
            )
        )

        return parametre.stream()
    }
}