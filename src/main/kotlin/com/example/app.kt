package com.example

import com.example.model.Entity
import com.example.model.article.Article
import com.example.model.user.User
import com.example.properties.BlogProperties
import com.example.properties.LiquibaseProperties
import com.example.repositories.*
import com.example.routes.*
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.ApplicationRunner
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

val json = configuration {
    webMvc {
        converters{
            jackson()
        }
    }
}

val liquibase = configuration {
    beans {
        bean {
            val liquibaseProperties: LiquibaseProperties = ref()

            SpringLiquibase().apply {
                changeLog = liquibaseProperties.changelogPath
                dataSource = ref()
            }
        }
    }
}

val app = webApplication {
    enable(mustache)
    enable(json)
    enable(h2)
    enable(blog)
    enable(api)
    enable(blogPersistence)
    enable(liquibase)
    configurationProperties<BlogProperties>(prefix = "blog")
    configurationProperties<LiquibaseProperties>(prefix = "liquibase")
    profile("dev"){
        beans {
            bean {
                ApplicationRunner{
                    val userRepository: UserRepository = ref()
                    val articleRepository: ArticleRepository = ref()
                    val luca = Entity.New(User.of("springluca", "Luca", "Piccinelli"))

                    val existingLuca = userRepository.save(luca)
                    articleRepository.save(Entity.New(
                        Article(
                            title = "Reactor Bismuth is out",
                            headline = "Lorem ipsum",
                            content = "dolor sit amet",
                            userFn = { existingLuca }
                        )
                    ))
                    articleRepository.save(
                        Entity.New(
                            Article(
                                title = "Reactor Aluminium has landed",
                                headline = "Lorem ipsum",
                                content = "dolor sit amet",
                                userFn = { existingLuca }
                            )
                        )
                    )
                }
            }
        }
    }
}

fun main(args: Array<String>){
    app.run(args)
}