package com.example.routes

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import com.example.repositories.ArticleEntity

object TestsEntitiesHelper {
    val luca = Entity.Existing(Id(0), User.of("springluca", "Luca", "Piccinelli"))
    val sebastien = Entity.Existing(Id(0), User.of("springsebastien", "Sébastien", "Deleuze"))

    val users = listOf(luca, sebastien)

    val articles = listOf(
        ArticleEntity(Id(0),
            Article(
                title = "Reactor Bismuth is out",
                headline = "Lorem ipsum",
                content = "dolor sit amet",
                userFn = { luca }
            )
        ),
        ArticleEntity(Id(1),
            Article(
                title = "Reactor Aluminium has landed",
                headline = "Lorem ipsum",
                content = "dolor sit amet",
                userFn = { luca }
            )
        )
    )
}