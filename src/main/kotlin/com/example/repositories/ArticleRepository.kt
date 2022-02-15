package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.ArticleInfo
import com.example.model.user.UserInfo

interface ArticleRepository{
    fun findByIdOrNull(id: Id<Int>): Entity.Existing<ArticleInfo<Entity.Existing<UserInfo>>>?
}