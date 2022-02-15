package com.example

import com.example.repositories.JdbcArticleRepositoryImpl
import com.example.repositories.JdbcUserRepositoryImpl
import com.example.routes.blog
import com.example.routes.blogPersistence
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.jdbc.DataSourceType
import org.springframework.fu.kofu.jdbc.jdbc
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc

val h2 = configuration {
    jdbc(DataSourceType.Hikari){
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
        driverClassName = "org.h2.Driver"
        username = "sa"
        password = ""
    }
}

val mustache = configuration {
    webMvc {
        mustache()
    }
}

val app = webApplication {
    enable(mustache)
    enable(h2)
    enable(blog)
    enable(blogPersistence)
}

fun main(args: Array<String>){
    app.run(args)
}