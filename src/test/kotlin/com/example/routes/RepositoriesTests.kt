package com.example.routes

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

class RepositoriesTests {

    private val dataSource: DataSource = DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
        .build()

    @Test
    fun `When findByLogin then return User`() {
        val luca = UserInfo.of("springluca", "Luca", "Piccinelli")
        val jdbcTemplate = JdbcTemplate(dataSource)

        jdbcTemplate.execute("""create table user(
                |id IDENTITY PRIMARY KEY, 
                |login VARCHAR NOT NULL,
                |firstname VARCHAR NOT NULL,
                |lastname VARCHAR NOT NULL,
                |description VARCHAR
            |)""".trimMargin())
        jdbcTemplate.execute("""insert into user(login,firstname,lastname) 
            |values ('${luca.login.value}', '${luca.name.firstname}', '${luca.name.lastname}')""".trimMargin())

        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val user = userRepository.findByLogin(luca.login)
        user?.info shouldBe luca
    }
}

class JdbcUserRepositoryImpl(dataSource: DataSource) {
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    fun findByLogin(login: Login): User? = jdbcTemplate.query(
        "select * from user where login=:login",
        mapOf("login" to login.value)){ rs, _ ->
        User(
            Id(rs.getInt("id")),
            UserInfo.of(
                rs.getString("login"),
                rs.getString("firstname"),
                rs.getString("lastname")))
    }
    .firstOrNull()
}

@JvmInline
value class Login(val value: String)

data class Id<T>(val value: T)

data class Name(
    val firstname: String,
    val lastname: String,
)

data class User(val id: Id<Int>, val info: UserInfo)

data class UserInfo(
    val login: Login,
    val name: Name,
    val description: String? = null){

    companion object{
        fun of(
            login: String,
            firstname: String,
            lastname: String,
            description: String? = null) =
            UserInfo(Login(login), Name(firstname, lastname), description)
    }
}