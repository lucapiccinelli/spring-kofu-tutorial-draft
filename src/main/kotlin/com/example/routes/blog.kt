package com.example.routes

import com.example.repositories.ArticleRepository
import com.example.repositories.JdbcArticleRepositoryImpl
import com.example.repositories.JdbcUserRepositoryImpl
import org.springframework.fu.kofu.configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

val blog = configuration {
    beans {
        bean {
            val htmlHandler = HtmlHandler(ref())

            router {
                GET("/", htmlHandler::blog)
                GET("/article/{slug}", htmlHandler::article)
            }
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
                "articles" to articleRepository.findAllByOrderByAddedAtDesc().map { it.info }
            ),
        )

    fun article(request: ServerRequest): ServerResponse {
        return articleRepository
            .findBySlug(request.pathVariable("slug"))
            ?.let { article ->
                ServerResponse.ok().render(
                    "article", mapOf(
                        "title" to article.info.title,
                        "article" to article.info
                    )
                )
            }
            ?: ServerResponse.notFound().build()
    }
}

