package com.example.routes

import com.example.model.user.User
import com.example.properties.BlogProperties
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
            val htmlHandler = HtmlHandler(ref(), ref())

            GET("/", htmlHandler::blog)
            GET("/article/{slug}", htmlHandler::article)
        }
    }
    configurationProperties<BlogProperties>(prefix = "blog")
}

val blogPersistence = configuration {
    beans {
        beans {
            bean { JdbcUserRepositoryImpl(ref()) }
            bean { JdbcArticleRepositoryImpl(ref()) }
        }
    }
}

class HtmlHandler(
    private val blogProperties: BlogProperties,
    private val articleRepository: ArticleRepository
    ) {
    fun blog(request: ServerRequest): ServerResponse =
        ServerResponse.ok().render(
            "blog", mapOf(
                "title" to blogProperties.title,
                "banner" to blogProperties.banner,
                "articles" to articleRepository.findAllByOrderByAddedAtDesc().map { it.render() }
            ),
        )

    fun article(request: ServerRequest): ServerResponse {
        return articleRepository
            .findBySlug(request.pathVariable("slug"))
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

