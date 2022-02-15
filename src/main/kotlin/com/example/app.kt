package com.example

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import com.example.repositories.*
import com.example.routes.blog
import com.example.routes.blogPersistence
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

val app = webApplication {
    enable(mustache)
    enable(h2)
    enable(blog)
    enable(blogPersistence)
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