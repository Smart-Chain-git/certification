package com.sword.signature.model.configuration

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.IndexDefinition
import org.springframework.data.mongodb.core.index.IndexOperations
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.sword.signature.model.repository"])
class MongoConfiguration(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val mongoMappingContext: MongoMappingContext
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {
        LOGGER.info("Initialization of Mongo indices started.")
        val init = System.currentTimeMillis()
        val resolver = MongoPersistentEntityIndexResolver(mongoMappingContext)
        for (entity in mongoMappingContext.persistentEntities) {
            val clazz = entity.type
            if (clazz.isAnnotationPresent(Document::class.java)) {
                val indexOps  = mongoTemplate.indexOps(clazz)
                resolver.resolveIndexFor(clazz).forEach { indexDefinition  -> indexOps.ensureIndex(indexDefinition) }
            }
        }
        LOGGER.info("Initialization of Mongo indices finished in {}ms.", System.currentTimeMillis() - init)
    }

    /*@Bean
    fun mongock(springContext: ApplicationContext, mongoTemplate: MongoTemplate): SpringBootMongock {
        return SpringBootMongockBuilder(mongoTemplate, "com.sword.signature.model.changelog")
                .setApplicationContext(springContext)
                .setLockQuickConfig()
                .build()
    }*/

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
