package com.sword.signature.model.repository

import com.sword.signature.model.entity.MigrationEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface MigrationRepository : MongoRepository<MigrationEntity, String> {

}
