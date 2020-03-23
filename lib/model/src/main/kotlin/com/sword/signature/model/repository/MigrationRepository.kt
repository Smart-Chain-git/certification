package com.sword.signature.model.repository

import com.sword.signature.model.entity.MigrationEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MigrationRepository : ReactiveMongoRepository<MigrationEntity, String> {

}
