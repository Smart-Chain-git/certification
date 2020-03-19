package com.sword.signature.business.service

import com.mongodb.client.model.Filters.exists
import com.sword.signature.business.configuration.TestContextConfiguration
import org.bson.Document
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
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
    abstract val mongoTemplate: MongoTemplate

    fun importJsonDataset(path: Path) {
        val collections: Document = Document.parse(Files.readString(path))
        for (collection in collections.keys) {
            val documents: List<Document> = collections[collection] as List<Document>
            mongoTemplate.getCollection(collection).insertMany(documents)
        }
    }

    fun resetDatabase() {
        for (collectionName in mongoTemplate.collectionNames) {
            mongoTemplate.getCollection(collectionName).deleteMany(exists("_id"))
        }
    }
}
