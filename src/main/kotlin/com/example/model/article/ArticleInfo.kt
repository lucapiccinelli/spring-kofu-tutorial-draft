package com.example.model.article

import com.example.model.Entity
import com.example.model.user.UserInfo
import com.example.toSlug
import java.time.LocalDateTime

data class ArticleInfo<out T : Entity<UserInfo>>(
    val title: String,
    val headline: String,
    val content: String,
    val user: T,
    val slug: String = title.toSlug(),
    val generatedAt: LocalDateTime = LocalDateTime.now()
)