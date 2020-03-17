package com.sword.signature.model.repository

import com.sword.signature.model.entity.AccountEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : MongoRepository<AccountEntity, String>{

    fun findFirstByLoginOrEmail(loginOrEmail: String): AccountEntity?
}