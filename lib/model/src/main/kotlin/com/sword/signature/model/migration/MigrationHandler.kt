package com.sword.signature.model.migration

import com.mongodb.client.model.Filters
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.entity.MigrationEntity
import com.sword.signature.model.repository.MigrationRepository
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.InputStreamReader

/**
 * Handler of database migrations.
 * Apply migrations to database by loading JSON files.
 */
@Component
class MigrationHandler(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val migrationRepository: MigrationRepository,
    private val mongoMappingContext: MongoMappingContext
) {


    suspend fun initIndices() {
        val resolver = MongoPersistentEntityIndexResolver(mongoMappingContext)
        for (entity in mongoMappingContext.persistentEntities) {
            val clazz = entity.type
            if (clazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document::class.java)) {
                val indexOps = mongoTemplate.indexOps(clazz)
                resolver.resolveIndexFor(clazz)
                    .forEach { indexDefinition -> indexOps.ensureIndex(indexDefinition).awaitSingle() }
            }
        }

    }




    /**
     * Check already applied migrations and apply new migrations.
     */
    @Transactional
    suspend fun applyMigrations() {
        val appliedMigrations: List<MigrationEntity> = migrationRepository.findAll().collectList().awaitLast()!!
                .sortedWith(MigrationEntity.versionComparator())
        val migrations: List<Migration> = readMigrations()
        appliedMigrations.forEachIndexed { index, appliedMigration ->
            if (index + 1 > migrations.size) {
                LOGGER.error("Missing applied migration '{}'.")
                throw MissingAppliedMigrationException(appliedMigration.name)
            }
            val migration = migrations[index]
            if (migration.version != appliedMigration.version) {
                LOGGER.error("Try to apply migration '{}' before already applied migrations.")
                throw WrongMigrationOrderException(migration.version)
            }
            val migrationHash = hashMigration(migration.content)
            if (migrationHash != appliedMigration.hash) {
                LOGGER.error("Checksums do not match for migration '{}'.", migration.name)
                throw WrongMigrationChecksumException(migration.name, appliedMigration.hash, migrationHash)
            }
            LOGGER.info("Found applied migration: '{}'.", migration.name)
        }
        for (index in appliedMigrations.size until migrations.size) {
            val migration = migrations[index]
            applyMigration(migration.content)
            migrationRepository.insert(MigrationEntity(
                    name = migration.name,
                    version = migration.version,
                    hash = hashMigration(migration.content)
            )).block()
            LOGGER.info("New migration '{}' applied.", migration.name)
        }
        LOGGER.info("{} new migration(s) successfully applied.", migrations.size - appliedMigrations.size)
    }

    /**
     * Read the migrations in the migrations resource folder.
     */
    private fun readMigrations(): List<Migration> {
        val resources = PathMatchingResourcePatternResolver().getResources("classpath:migrations/*.json")
        val migrations = mutableListOf<Migration>()
        for (resource in resources) {
            val migrationContent = InputStreamReader(resource.inputStream).readText()
            migrations.add(Migration(
                    name = resource.filename!!,
                    version = extractVersion(resource.filename!!),
                    content = migrationContent
            ))
        }
        // Sort the migrations list by version and return it as an array
        return migrations.sortedWith(Migration.versionComparator())
    }

    /**
     * Parse and apply a migration.
     */
    @Transactional
    suspend fun applyMigration(migrationContent: String) {
        val collections = Document.parse(migrationContent)
        for (collectionName in collections.keys) {
            val collection: Document = collections[collectionName] as Document
            // Insert operations
            val documentsToInsert = collection["insert"] as List<Document>?
            documentsToInsert?.let { mongoTemplate.getCollection(collectionName).insertMany(it).awaitLast() }
            // Update operations
            val documentsToUpdate = collection["update"] as List<Document>?
            documentsToUpdate?.let {
                it.forEach { document ->
                    mongoTemplate.getCollection(collectionName).replaceOne(Filters.eq("_id", document["_id"]), document).awaitLast()
                }
            }
            // Delete
            val documentToDelete = collection["delete"] as List<Document>?
            documentToDelete?.let { documents ->
                mongoTemplate.getCollection(collectionName).deleteMany(Filters.`in`("_id", documents.map { it["_id"] })).awaitLast()
            }
        }

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MigrationHandler::class.java)

        /**
         * Hash the migration content.
         */
        private fun hashMigration(migrationContent: String): String {
            return hexStringHash("SHA-256", migrationContent.toByteArray())
        }

        /**
         * Extract the version from the migration file name.
         */
        private fun extractVersion(migrationName: String): String {
            return migrationName.split("_")[0]
        }
    }
}