package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.ArticleInfo
import com.example.model.user.UserInfo
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class JdbcArticleRepositoryImpl(dataSource: DataSource) : ArticleRepository {
    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val userRepository = JdbcUserRepositoryImpl(dataSource)

    override fun findByIdOrNull(id: Id<Int>): Entity.Existing<ArticleInfo<Entity.Existing<UserInfo>>>? = jdbcTemplate
        .query("select * from article where id=${id.value}") { rs, _ ->
            userRepository
                .findByIdOrNull(rs.getInt("user_id").run(::Id))
                ?.let { user ->
                    Entity.Existing(
                        Id(rs.getInt("id")),
                        ArticleInfo(
                            rs.getString("title"),
                            rs.getString("headline"),
                            rs.getString("content"),
                            user,
                            rs.getString("slug"),
                            rs.getTimestamp("added_at").toLocalDateTime()
                        )
                    )
                }
        }
        .firstOrNull()
}