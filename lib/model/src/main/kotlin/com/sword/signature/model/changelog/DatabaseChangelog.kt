package com.sword.signature.model.changelog

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.sword.signature.model.entity.AccountEntity
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeLog
class DatabaseChangelog {
    @ChangeSet(order = "001", id = "addAdminAccount", author = "lib")
    fun addAdminAccount(mongoTemplate: MongoTemplate) {
        LOGGER.trace("Creating admin account.")
        val adminAccount = AccountEntity(
                login = "admin",
                email = "admin@sword-signature.com",
                password = "password", //FIXME: encrypted password
                fullName = "Administrator"
        )
        mongoTemplate.save(adminAccount)
        LOGGER.trace("Admin account created.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
