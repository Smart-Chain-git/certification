package com.sword.signature.model.repository


import com.sword.signature.model.entity.TreeElementEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface TreeElementRepository :
    ReactiveMongoRepository<TreeElementEntity, String>,
    ReactiveQuerydslPredicateExecutor<TreeElementEntity>