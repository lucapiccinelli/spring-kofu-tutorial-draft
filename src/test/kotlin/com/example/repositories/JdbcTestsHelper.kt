package com.example.repositories

import com.example.model.Entity
import com.example.model.article.Article
import com.example.model.user.User
import com.example.utils.JdbcSchemaCreator
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource

class JdbcTestsHelper(private val dataSource: DataSource) {
    companion object{
        fun getDataSource(): DataSource =
            DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .build()

        val luca = User.of("springluca", "Luca", "Piccinelli")
        val article1 = Article(
            "Spring Kotlin DSL is amazing",
            "Dear Spring community ...",
            "Lorem ipsum",
            { Entity.New(luca) })
    }

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcSchemaCreator = JdbcSchemaCreator(dataSource)

    private val insertUser = SimpleJdbcInsert(dataSource)
        .withTableName("user")
        .usingGeneratedKeyColumns("id")

    private val insertArticle = SimpleJdbcInsert(dataSource)
        .withTableName("article")
        .usingGeneratedKeyColumns("id")

    private fun insertUser(user: User): Number = insertUser.executeAndReturnKey(mapOf(
        "login" to user.login.value,
        "firstname" to user.name.firstname,
        "lastname" to user.name.lastname)
    )

    fun insertArticle(article: Article<Entity.New<User>>): Pair<Number, Number> {
        val userId = insertUser(article.user.info)

        return userId to insertArticle.executeAndReturnKey(mapOf(
            "title" to article.title,
            "headline" to article.headline,
            "content" to article.content,
            "slug" to article.slug,
            "added_at" to article.addedAt,
            "user_id" to userId)
        )
    }

    fun createUserTable() = jdbcSchemaCreator.createUserTable()

    fun createArticleTable() = jdbcSchemaCreator.createArticleTable()

    fun dropUserTable(){
        jdbcTemplate.execute("drop table user")
    }

    fun dropArticleTable(){
        jdbcTemplate.execute("drop table article")
    }
}
