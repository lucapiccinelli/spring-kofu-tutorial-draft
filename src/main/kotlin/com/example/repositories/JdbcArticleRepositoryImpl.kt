package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.ArticleInfo
import com.example.model.user.UserInfo
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class JdbcArticleRepositoryImpl(dataSource: DataSource) : ArticleRepository {
    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val userRepository = JdbcUserRepositoryImpl(dataSource)

    override fun findByIdOrNull(id: Id<Int>): Entity.Existing<ArticleInfo<Entity.Existing<UserInfo>>>? = jdbcTemplate
        .query("select * from article where id=${id.value}") { rs, _ ->
            val userId = rs.getInt("user_id")
            Entity.Existing(
                Id(rs.getInt("id")),
                ArticleInfo(
                    rs.getString("title"),
                    rs.getString("headline"),
                    rs.getString("content"),
                    {
                        userRepository
                            .findByIdOrNull(userId.run(::Id))
                            ?: throw DataRetrievalFailureException("On article with id ${id.value} There is no user with id $userId")
                    },
                    rs.getString("slug"),
                    rs.getTimestamp("added_at").toLocalDateTime()
                )
            )
        }
        .firstOrNull()
}