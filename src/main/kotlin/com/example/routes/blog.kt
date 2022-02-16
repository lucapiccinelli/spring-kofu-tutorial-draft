package com.example.routes

import com.example.format
import com.example.model.Entity
import com.example.model.article.Article
import com.example.model.user.User
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

fun Article<Entity<User>>.render() = RenderedArticle(
    slug,
    title,
    headline,
    content,
    user.info.render(),
    addedAt.format()
)

fun User.render() = RenderedUser(login.value, name.firstname, name.lastname, description)

data class RenderedArticle(
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val user: RenderedUser,
    val addedAt: String)

data class RenderedUser(
    val login: String,
    val firstname: String,
    val lastname: String,
    val description: String?)

