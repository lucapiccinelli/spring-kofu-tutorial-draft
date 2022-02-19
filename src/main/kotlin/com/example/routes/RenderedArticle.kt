package com.example.routes

import com.example.format
import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import com.example.repositories.ArticleEntity

data class RenderedArticle(
    val id: Int,
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val user: RenderedUser,
    val addedAt: String)


fun ArticleEntity.render() = RenderedArticle(
    id.value,
    info.slug,
    info.title,
    info.headline,
    info.content,
    info.user.render(),
    info.addedAt.format()
)