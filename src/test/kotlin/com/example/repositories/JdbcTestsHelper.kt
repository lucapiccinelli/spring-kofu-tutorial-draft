package com.example.repositories

import com.example.model.Entity
import com.example.model.article.ArticleInfo
import com.example.model.user.UserInfo
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

        val luca = UserInfo.of("springluca", "Luca", "Piccinelli")
        val article1 = ArticleInfo(
            "Spring Kotlin DSL is amazing",
            "Dear Spring community ...",
            "Lorem ipsum",
            Entity.New(luca))
    }

    private val jdbcTemplate = JdbcTemplate(dataSource)

    private val insertUser = SimpleJdbcInsert(dataSource)
        .withTableName("user")
        .usingGeneratedKeyColumns("id")

    private val insertArticle = SimpleJdbcInsert(dataSource)
        .withTableName("article")
        .usingGeneratedKeyColumns("id")

    private fun insertUser(user: UserInfo): Number = insertUser.executeAndReturnKey(mapOf(
        "login" to user.login.value,
        "firstname" to user.name.firstname,
        "lastname" to user.name.lastname)
    )

    fun insertArticle(article: ArticleInfo<Entity.New<UserInfo>>): Pair<Number, Number> {
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

    fun createUserTable() {
        jdbcTemplate.execute(
            """create table if not exists user(
                    |id IDENTITY PRIMARY KEY, 
                    |login VARCHAR NOT NULL,
                    |firstname VARCHAR NOT NULL,
                    |lastname VARCHAR NOT NULL,
                    |description VARCHAR
                |)""".trimMargin()
        )
    }

    fun createArticleTable() {
        jdbcTemplate.execute(
            """create table if not exists article(
                    |id IDENTITY PRIMARY KEY, 
                    |title VARCHAR NOT NULL,
                    |headline VARCHAR NOT NULL,
                    |content VARCHAR NOT NULL,
                    |slug VARCHAR NOT NULL,
                    |added_at DATETIME,
                    |user_id INT NOT NULL,
                    |constraint FK_USER foreign key (user_id) references user(id)
                |)""".trimMargin()
        )
    }

    fun dropUserTable(){
        jdbcTemplate.execute("drop table user")
    }

    fun dropArticleTable(){
        jdbcTemplate.execute("drop table article")
    }
}