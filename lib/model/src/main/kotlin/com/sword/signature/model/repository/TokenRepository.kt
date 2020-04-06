package com.sword.signature.model.repository;

import com.sword.signature.model.entity.TokenEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : ReactiveMongoRepository<TokenEntity, String> {

    suspend fun findByJwtToken(jwtToken: String): TokenEntity?
}
