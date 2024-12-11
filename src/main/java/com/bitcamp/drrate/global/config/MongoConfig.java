package com.bitcamp.drrate.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.bitcamp.drrate.domain.inquire")
public class MongoConfig {
}