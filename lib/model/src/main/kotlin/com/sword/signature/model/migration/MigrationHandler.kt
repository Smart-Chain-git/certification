package com.sword.signature.model.migration

import com.mongodb.client.model.Filters
import com.sword.signature.merkletree.utils.hexStringHash
import com.sword.signature.model.entity.MigrationEntity
import com.sword.signature.model.repository.MigrationRepository
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest

/**
 * Handler of database migrations.
 * Apply migrations to database by loading JSON files.
 */
@Component
class MigrationHandler(
        private val mongoTemplate: MongoTemplate,
        private val migrationRepository: MigrationRepository
) {
    private val digest = MessageDigest.getInstance("SHA-256")

    /**
     * Check already applied migrations and apply new migrations.
     */
    @Transactional
    fun applyMigrations() {
        val appliedMigrations = migrationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
        val migrations: Array<Migration> = readMigrations()
        appliedMigrations.forEachIndexed { index, appliedMigration ->
            if (index + 1 > migrations.size) {
                LOGGER.error("Missing applied migration '{}'.")
                throw MissingAppliedMigrationException(appliedMigration.name)
            }
            val migration = migrations[index]
            if (migration.name != appliedMigration.name) {
                LOGGER.error("Try to apply migration '{}' before already applied migrations.")
                throw WrongMigrationOrderException(migration.name)
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
                    hash = hashMigration(migration.content)
            ))
            LOGGER.info("New migration '{}' applied.", migration.name)
        }
        LOGGER.info("{} new migration(s) successfully applied.", migrations.size - appliedMigrations.size)
    }

    /**
     * Read the migrations in the migrations resource folder.
     */
    private fun readMigrations(): Array<Migration> {
        val resources = PathMatchingResourcePatternResolver().getResources("classpath:migrations/*.json")
        resources.sortBy { it.filename }
        val migrations = mutableListOf<Migration>()
        for (resource in resources) {
            val bufferedReader = BufferedReader(InputStreamReader(resource.inputStream))
            var line: String?
            val builder = StringBuilder()
            bufferedReader.use { br ->
                while (br.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                migrations.add(Migration(name = resource.filename!!, content = builder.toString()))
            }
        }
        return migrations.toTypedArray()
    }

    /**
     * Hash the migration content.
     */
    private fun hashMigration(migrationContent: String): String {
        return hexStringHash("SHA-256", migrationContent.toByteArray())
    }

    /**
     * Parse and apply a migration.
     */
    @Transactional
    fun applyMigration(migrationContent: String) {
        val collections = Document.parse(migrationContent)
        for (collectionName in collections.keys) {
            val collection: Document = collections[collectionName] as Document
            // Insert operations
            val documentsToInsert = collection["insert"] as List<Document>?
            documentsToInsert?.let { mongoTemplate.getCollection(collectionName).insertMany(it) }
            // Update operations
            val documentsToUpdate = collection["update"] as List<Document>?
            documentsToUpdate?.let {
                it.forEach { document ->
                        mongoTemplate.getCollection(collectionName).replaceOne(Filters.eq("_id", document["_id"]), document)
                }
            }
            // Delete
            val documentToDelete = collection["delete"] as List<Document>?
            documentToDelete?.let {documents ->
                mongoTemplate.getCollection(collectionName).deleteMany(Filters.`in`("_id", documents.map { it["_id"] }))
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MigrationHandler::class.java)
    }
}