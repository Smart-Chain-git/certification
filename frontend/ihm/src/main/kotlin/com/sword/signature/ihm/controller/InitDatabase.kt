package com.sword.signature.ihm.controller

import com.sword.signature.business.model.AccountCreate
import com.sword.signature.business.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@ComponentScan(basePackages = arrayOf("com.sword.signature"))
@EnableMongoRepositories(basePackages = arrayOf("com.sword.signature.model.repository"))
class InitDatabase(
        @Autowired private val accountService: AccountService
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val accountDetails = AccountCreate(
                login = "test",
                email = "test@solo.org",
                password = "password",
                fullName = "Test Solo"
        )
        val createdAccount = accountService.createAccount(accountDetails)
        println(createdAccount.id)
    }
}