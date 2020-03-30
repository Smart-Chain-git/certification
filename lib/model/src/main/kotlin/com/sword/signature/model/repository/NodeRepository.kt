package com.sword.signature.model.repository

import com.sword.signature.model.entity.NodeEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NodeRepository : ReactiveMongoRepository<NodeEntity, String>