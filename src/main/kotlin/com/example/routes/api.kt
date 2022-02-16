package com.example.routes

import com.example.model.user.Login
import com.example.repositories.ArticleRepository
import com.example.repositories.UserRepository
import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

val api = configuration {
    beans {
        bean {
            router {
                val articleHandler = ArticleHandler(ref())
                val userHandler = UserHandler(ref())
                "/api".nest {
                    "/article".nest {
                        GET("/", articleHandler::findAll)
                        GET("/{slug}", articleHandler::findOne)
                    }
                    "/user".nest {
                        GET("/", userHandler::findAll)
                        GET("/{login}", userHandler::findOne)
                    }
                }

            }
        }
    }
}

class UserHandler(private val userRepository: UserRepository) {
    fun findAll(serverRequest: ServerRequest): ServerResponse = userRepository.findAll().run(::ok)

    fun findOne(serverRequest: ServerRequest): ServerResponse = userRepository
        .findByLogin(serverRequest.pathVariable("login").run(::Login))
        ?.run(::ok)
        ?: ServerResponse.notFound().build()
}

class ArticleHandler(private val articleRepository: ArticleRepository) {
    fun findAll(serverRequest: ServerRequest): ServerResponse = articleRepository.findAllByOrderByAddedAtDesc().run(::ok)

    fun findOne(serverRequest: ServerRequest): ServerResponse = articleRepository
        .findBySlug(serverRequest.pathVariable("slug"))
        ?.run(::ok)
        ?: ServerResponse.notFound().build()
}

private fun ok(body: Any): ServerResponse = ServerResponse.ok().body(body)
