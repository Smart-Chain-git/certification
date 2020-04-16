package com.sword.signature.model.repository

import com.sword.signature.model.entity.JobEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository : ReactiveMongoRepository<JobEntity, String> {

    fun findAllByUserId(userId: String): Flow<JobEntity>

}