package com.example.routes

import com.example.format
import com.example.model.Entity
import com.example.model.article.Article
import com.example.model.user.User

data class RenderedArticle(
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val user: RenderedUser,
    val addedAt: String)


fun Article<Entity<User>>.render() = RenderedArticle(
    slug,
    title,
    headline,
    content,
    user.info.render(),
    addedAt.format()
)