package com.example.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("liquibase")
data class LiquibaseProperties(
    var changelogPath: String
)
