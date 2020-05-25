package com.sword.signature.model.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.sword.signature.model.repository"])
class MongoConfiguration
