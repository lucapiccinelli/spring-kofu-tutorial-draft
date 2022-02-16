package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcArticleRepositoryImpl(dataSource: DataSource) : ArticleRepository {
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val userRepository = JdbcUserRepositoryImpl(dataSource)

    private val insert = SimpleJdbcInsert(dataSource)
        .withTableName("article")
        .usingGeneratedKeyColumns("id")

    override fun findByIdOrNull(id: Id<Int>): ArticleEntity? = firstOrNull("id", id.value)
    override fun findBySlug(slug: String): ArticleEntity? = firstOrNull("slug", slug)

    override fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity> = jdbcTemplate
        .query("select * from article order by added_at desc") { rs, _ ->
            toArticle(rs)
        }

    override fun save(article: Entity<Article<Entity<User>>>): ArticleEntity {
        val user: Entity.Existing<User> = when (val user = article.info.user) {
            is Entity.New -> userRepository.save(user)
            is Entity.Existing -> user
        }

        val parameters = with(article.info) {
            mapOf(
                "title" to title,
                "headline" to headline,
                "slug" to slug,
                "added_at" to addedAt,
                "content" to content,
                "user_id" to user.id.value,
            )
        }

        return when (article) {
            is Entity.New -> {
                insert
                    .executeAndReturnKey(parameters)
                    .let { id -> article.info.withUser(user).existing(id.toInt().run(::Id)) }
            }
            is Entity.Existing -> jdbcTemplate
                .update(
                    """update article set
                        |title=:title, 
                        |headline=:headline, 
                        |slug=:slug,
                        |added_at=:added_at, 
                        |content=:content,
                        |user_id=:user_id
                        |where id=:id""".trimMargin(),
                    parameters.toMutableMap<String, Any>().also { it["id"] = "${article.id.value}" })
                .let { Entity.Existing(article.id, article.info.withUser(user)) }
        }
    }

    private fun toArticle(rs: ResultSet): ArticleEntity {
        val userId: Int = rs.getInt("user_id")
        val articleId: Int = rs.getInt("id")
        return Entity.Existing(
            Id(articleId),
            Article(
                rs.getString("title"),
                rs.getString("headline"),
                rs.getString("content"),
                {
                    userRepository
                        .findByIdOrNull(userId.run(::Id))
                        ?: throw DataRetrievalFailureException("On article with id $articleId There is no user with id $userId")
                },
                rs.getString("slug"),
                rs.getTimestamp("added_at").toLocalDateTime()
            )
        )
    }

    private fun firstOrNull(parameterName: String, value: Any) = jdbcTemplate
        .query("select * from article where $parameterName=:$parameterName", mapOf(parameterName to value)) { rs, _ ->
            toArticle(rs)
        }
        .firstOrNull()
}