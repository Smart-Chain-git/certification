package com.sword.signature.model.configuration

import com.sword.signature.model.migration.MigrationHandler
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.sword.signature.model.repository"])
class MongoConfiguration(
        private val mongoTemplate: ReactiveMongoTemplate,
        private val mongoMappingContext: MongoMappingContext,
        private val migrationHandler: MigrationHandler
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initDatabase() {
        runBlocking {
            initIndicesAfterStartup()
            insertMigrationsData()
        }
    }


    private suspend fun initIndicesAfterStartup() {
        LOGGER.info("Initialization of Mongo indices started.")
        val init = System.currentTimeMillis()
        val resolver = MongoPersistentEntityIndexResolver(mongoMappingContext)
        for (entity in mongoMappingContext.persistentEntities) {
            val clazz = entity.type
            if (clazz.isAnnotationPresent(Document::class.java)) {
                val indexOps = mongoTemplate.indexOps(clazz)
                resolver.resolveIndexFor(clazz).forEach { indexDefinition -> indexOps.ensureIndex(indexDefinition).awaitSingle() }
            }
        }
        LOGGER.info("Initialization of Mongo indices finished in {}ms.", System.currentTimeMillis() - init)
    }

    private suspend fun insertMigrationsData() {
        LOGGER.info("Application of migrations data started.")
        val init = System.currentTimeMillis()
        migrationHandler.applyMigrations()
        LOGGER.info("Application of migrations data finished in {}ms.", System.currentTimeMillis() - init)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MongoConfiguration::class.java)
    }
}
