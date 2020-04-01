package com.sword.signature.model.repository

import com.sword.signature.model.entity.AlgorithmEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface AlgorithmRepository: ReactiveMongoRepository<AlgorithmEntity, String> {

    suspend fun findByName(name: String): AlgorithmEntity?
}