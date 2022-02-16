package com.example.utils

import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class JdbcSchemaCreator(private val dataSource: DataSource){
    private val jdbcTemplate = JdbcTemplate(dataSource)

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
}