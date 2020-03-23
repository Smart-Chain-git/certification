package com.sword.signature.model.configuration

import com.sword.signature.model.migration.MigrationHandler
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexDefinition
import org.springframework.data.mongodb.core.index.IndexOperations
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@Configuration
@EnableMongoRepositories(basePackages = ["com.sword.signature.model.repository"])
class MongoConfiguration(
        private val mongoTemplate: MongoTemplate,
        private val mongoMappingContext: MongoMappingContext,
        private val migrationHandler: MigrationHandler
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initDatabase() {
        initIndicesAfterStartup()
        insertMigrationsData()
    }


    fun initIndicesAfterStartup() {
        LOGGER.info("Initialization of Mongo indices started.")
        val init = System.currentTimeMillis()
        val resolver = MongoPersistentEntityIndexResolver(mongoMappingContext)
        for (entity in mongoMappingContext.persistentEntities) {
            val clazz = entity.type
            if (clazz.isAnnotationPresent(Document::class.java)) {
                val indexOps: IndexOperations = mongoTemplate.indexOps(clazz)
                resolver.resolveIndexFor(clazz).forEach { indexDefinition: IndexDefinition? -> indexOps.ensureIndex(indexDefinition!!) }
            }
        }
        LOGGER.info("Initialization of Mongo indices finished in {}ms.", System.currentTimeMillis() - init)
    }

    private fun insertMigrationsData() {
        LOGGER.info("Application of migrations data started.")
        val init = System.currentTimeMillis()
        migrationHandler.applyMigrations()
        LOGGER.info("Application of migrations data finished in {}ms.", System.currentTimeMillis() - init)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MongoConfiguration::class.java)
    }
}
