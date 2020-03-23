package com.sword.signature.business.service

import com.mongodb.client.model.Filters.exists
import com.sword.signature.business.configuration.TestContextConfiguration
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Files
import java.nio.file.Path


@DataMongoTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test", "context")
@ContextConfiguration(classes = [TestContextConfiguration::class])
abstract class AbstractServiceContextTest {
    abstract val mongoTemplate: ReactiveMongoTemplate

    fun importJsonDataset(path: Path) {
        val collections  = Document.parse(Files.readString(path))
        runBlocking {
            collections.keys.forEach { collection ->
                val documents = collections[collection] as List<Document>
                mongoTemplate.getCollection(collection).insertMany(documents).awaitSingle()
            }
        }
    }


    fun resetDatabase() {
        runBlocking {
            for (collectionName in mongoTemplate.collectionNames.toIterable()) {
                mongoTemplate.getCollection(collectionName).deleteMany(exists("_id")).awaitSingle()
            }
        }
    }
}

