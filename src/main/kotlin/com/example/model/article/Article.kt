package com.example.model.article

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.User
import com.example.toSlug
import java.time.LocalDateTime

data class Article<out T : Entity<User>>(
    val title: String,
    val headline: String,
    val content: String,
    private val userFn: () -> T,
    val slug: String = title.toSlug(),
    val addedAt: LocalDateTime = LocalDateTime.now().withNano(0)
){
    fun <U: Entity<User>> withUser(user: U): Article<U> =
        Article(title, headline, content, { user }, slug, addedAt)

    fun existing(id: Id<Int>) = Entity.Existing(id, Article(title, headline, content, userFn, slug, addedAt))

    val user by lazy(userFn)
}