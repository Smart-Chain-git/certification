package com.sword.signature.model.repository;

import com.sword.signature.model.entity.TokenEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : ReactiveMongoRepository<TokenEntity, String>,
    ReactiveQuerydslPredicateExecutor<TokenEntity> {
    fun findAllByAccountId(accountId: String): Flow<TokenEntity>
}
