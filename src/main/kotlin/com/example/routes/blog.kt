package com.example.routes

import com.example.model.user.User
import com.example.repositories.ArticleRepository
import com.example.repositories.JdbcArticleRepositoryImpl
import com.example.repositories.JdbcUserRepositoryImpl
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

val blog = configuration {
    webMvc {
        router {
            val htmlHandler = HtmlHandler(ref())

            GET("/", htmlHandler::blog)
            GET("/article/{slug}", htmlHandler::article)
        }
    }
}

val blogPersistence = configuration {
    beans {
        beans {
            bean { JdbcUserRepositoryImpl(ref()) }
            bean { JdbcArticleRepositoryImpl(ref()) }
        }
    }
}

class HtmlHandler(private val articleRepository: ArticleRepository) {
    fun blog(request: ServerRequest): ServerResponse =
        ServerResponse.ok().render(
            "blog", mapOf(
                "title" to "Blog",
                "articles" to articleRepository.findAllByOrderByAddedAtDesc().map { it.info.render() }
            ),
        )

    fun article(request: ServerRequest): ServerResponse {
        return articleRepository
            .findBySlug(request.pathVariable("slug"))
            ?.info
            ?.render()
            ?.let { article ->
                ServerResponse.ok().render(
                    "article", mapOf(
                        "title" to article.title,
                        "article" to article
                    )
                )
            }
            ?: ServerResponse.notFound().build()
    }
}

