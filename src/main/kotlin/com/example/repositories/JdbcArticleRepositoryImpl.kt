package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.User
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcArticleRepositoryImpl(dataSource: DataSource) : ArticleRepository {
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val userRepository = JdbcUserRepositoryImpl(dataSource)

    override fun findByIdOrNull(id: Id<Int>): ArticleEntity? = firstOrNull("id", id.value)
    override fun findBySlug(slug: String): ArticleEntity? = firstOrNull("slug", slug)

    override fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity> = jdbcTemplate
        .query("select * from article order by added_at desc") { rs, _ ->
            toArticle(rs)
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