package com.sword.signature.daemon.configuration

import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
@ComponentScan(basePackages = ["com.sword.signature"])
class DaemonConfiguration {


    // la base de données mémoire de spring batch (on ne garde pas les batch d'un run à l'autre, on bosse avec les fichiers traces
    @Bean(name = ["batchDataSource"])
    @ConfigurationProperties(prefix = "spring.batch.datasource")
    fun batchDataSource(): DataSource {
        logger.info("creation base hql pour les job")
        return DataSourceBuilder.create().build()

    }

    @Bean
    fun customBatchConfigurer(@Qualifier("batchDataSource") batchDataSource: DataSource) =
        DefaultBatchConfigurer(batchDataSource)


    companion object {
        private val logger = LoggerFactory.getLogger(DaemonConfiguration::class.java)
    }

}