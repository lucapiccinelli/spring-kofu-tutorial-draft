package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User

interface ArticleRepository{
    fun findByIdOrNull(id: Id<Int>): Entity.Existing<Article<Entity.Existing<User>>>?
}