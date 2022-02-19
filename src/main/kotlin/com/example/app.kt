package com.example

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import com.example.properties.BlogProperties
import com.example.repositories.*
import com.example.routes.*
import com.example.utils.JdbcSchemaCreator
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.jdbc.DataSourceType
import org.springframework.fu.kofu.jdbc.jdbc
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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

val app = webApplication {
    enable(mustache)
    enable(json)
    enable(h2)
    enable(blog)
    enable(api)
    enable(blogPersistence)
    configurationProperties<BlogProperties>(prefix = "blog")
    beans {
        bean {
            ApplicationRunner {
                JdbcSchemaCreator(ref()).apply {
                    createUserTable()
                    createArticleTable()
                }
            }
        }
    }
    profile("dev"){
        beans {
            bean {
                ApplicationRunner{
                    val userRepository: UserRepository = ref()
                    val articleRepository: ArticleRepository = ref()
                    val luca = Entity.New(User.of("springluca", "Luca", "Piccinelli"))

                    userRepository.save(luca)
                    articleRepository.save(Entity.New(
                        Article(
                            title = "Reactor Bismuth is out",
                            headline = "Lorem ipsum",
                            content = "dolor sit amet",
                            userFn = { luca }
                        )
                    ))
                    articleRepository.save(
                        Entity.New(
                            Article(
                                title = "Reactor Aluminium has landed",
                                headline = "Lorem ipsum",
                                content = "dolor sit amet",
                                userFn = { luca }
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