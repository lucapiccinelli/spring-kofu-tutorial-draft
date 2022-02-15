package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User

typealias ArticleEntity = Entity.Existing<Article<Entity.Existing<User>>>

interface ArticleRepository{
    fun findByIdOrNull(id: Id<Int>): ArticleEntity?
    fun findBySlug(slug: String): ArticleEntity?
    fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity>
}