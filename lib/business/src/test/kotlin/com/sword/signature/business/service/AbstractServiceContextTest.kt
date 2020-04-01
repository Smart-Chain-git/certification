package com.sword.signature.business.service

import com.sword.signature.business.configuration.TestContextConfiguration
import com.sword.signature.model.configuration.MongoConfiguration
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
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
    abstract val mongoConfiguration: MongoConfiguration

    fun importJsonDataset(path: Path) {
        val collections = Document.parse(Files.readString(path))
        runBlocking {
            collections.keys.forEach { collection ->
                val documents = collections[collection] as List<Document>
                mongoTemplate.getCollection(collection).insertMany(documents).awaitLast()
            }
        }
    }

    fun resetDatabase() {
        runBlocking {
            // Drop all collections.
            for (collectionName in mongoTemplate.collectionNames.toIterable()) {
                mongoTemplate.dropCollection(collectionName).awaitFirstOrNull()
            }
        }
        // Init the migration (indices + migrations).
        mongoConfiguration.initDatabase()
    }
}

