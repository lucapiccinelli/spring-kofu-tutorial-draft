package com.example.routes

import com.example.repositories.ArticleRepository
import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

val blog = configuration {
    beans {
        bean {
            val htmlHandler = HtmlHandler(ref())

            router {
                GET("/", htmlHandler::blog)
            }
        }
    }
}

class HtmlHandler(private val articleRepository: ArticleRepository) {
    fun blog(request: ServerRequest): ServerResponse =
        ServerResponse.ok().render("blog", mapOf("title" to "my blog"))
}

