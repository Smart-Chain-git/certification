package com.sword.signature.model.repository;

import com.sword.signature.model.entity.TokenEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TokenRepository : ReactiveMongoRepository<TokenEntity, String> {

    fun findByJwtToken(jwtToken: String): Mono<TokenEntity>
    fun findByAccountId(accountId: String): Flux<TokenEntity>
}
