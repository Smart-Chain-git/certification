package com.sword.signature.business.service

import com.sword.signature.business.configuration.TestContextConfiguration
import com.sword.signature.model.configuration.MongoConfiguration
import com.sword.signature.model.migration.MigrationHandler
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Files
import java.nio.file.Path


@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(classes = [TestContextConfiguration::class])
abstract class AbstractServiceContextTest() {
    abstract val mongoTemplate: ReactiveMongoTemplate
    abstract val migrationHandler : MigrationHandler

    fun importJsonDatasets(vararg paths: Path) {
        paths.forEach {
            val collections = Document.parse(Files.readString(it))
            runBlocking {
                collections.keys.forEach { collection ->
                    val documents = collections[collection] as List<Document>
                    mongoTemplate.getCollection(collection).awaitSingle().insertMany(documents).awaitLast()
                }
            }
        }
    }

    fun resetDatabase() {
        runBlocking {
            // Drop all collections.
            for (collectionName in mongoTemplate.collectionNames.toIterable()) {
                mongoTemplate.dropCollection(collectionName).awaitFirstOrNull()
            }

            // Init the migration (indices + migrations).
            migrationHandler.initIndices()
            migrationHandler.applyMigrations()
        }

    }
}

