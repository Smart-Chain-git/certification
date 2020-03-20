package com.sword.signature.model.repository

import com.sword.signature.model.entity.AccountEntity
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : ReactiveMongoRepository<AccountEntity, String> {


    @Query("{ \$or: [ { login: ?0 }, { email: ?0 } ] }")
    suspend fun findFirstByLoginOrEmail(loginOrEmail: String): AccountEntity?
}