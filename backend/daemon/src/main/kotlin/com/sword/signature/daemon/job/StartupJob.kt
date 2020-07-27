package com.sword.signature.daemon.job

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupJob(
    private val initDataBaseJob: InitDataBaseJob,
    private val recoverJob: RecoverJob
) {

    @EventListener(ApplicationReadyEvent::class)
    fun executeStartupJobs() {
        LOGGER.info("Execute startup jobs...")
        LOGGER.info("Execute initDatabase job.")
        initDataBaseJob.initDatabase()
        LOGGER.info("Execute recover job.")
        recoverJob.recoverInjectedJobs()
        LOGGER.info("All startup jobs fully executed.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StartupJob::class.java)
    }
}