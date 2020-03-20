package com.sword.signature.model.repository

import com.sword.signature.model.entity.AccountEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : MongoRepository<AccountEntity, String>{


    @Query("{ \$or: [ { login: ?0 }, { email: ?0 } ] }")
    fun findFirstByLoginOrEmail(loginOrEmail: String): AccountEntity?
}