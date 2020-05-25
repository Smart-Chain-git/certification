package com.sword.signature.daemon.job

import com.sword.signature.model.migration.MigrationHandler
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class InitDataBaseJob(
    private val migrationHandler : MigrationHandler
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
        migrationHandler.initIndices()
        LOGGER.info("Initialization of Mongo indices finished in {}ms.", System.currentTimeMillis() - init)
    }


    private suspend fun insertMigrationsData() {
        LOGGER.info("Application of migrations data started.")
        val init = System.currentTimeMillis()
        migrationHandler.applyMigrations()
        LOGGER.info("Application of migrations data finished in {}ms.", System.currentTimeMillis() - init)
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(InitDataBaseJob::class.java)
    }

}