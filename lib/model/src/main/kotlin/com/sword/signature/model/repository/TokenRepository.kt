package com.sword.signature.model.repository;

import com.sword.signature.model.entity.TokenEntity
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TokenRepository : ReactiveMongoRepository<TokenEntity, String> {

    suspend fun findByJwtToken(jwtToken: String): TokenEntity?
    @Query("{'accountId' : ?0}")
    suspend fun findAllByAccountId(accountId: String): Flux<TokenEntity>
}
