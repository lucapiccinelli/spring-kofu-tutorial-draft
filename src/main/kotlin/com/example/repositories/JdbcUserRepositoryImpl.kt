package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.Login
import com.example.model.user.UserInfo
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource

class JdbcUserRepositoryImpl(dataSource: DataSource) : UserRepository {
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val insertUser = SimpleJdbcInsert(dataSource)
        .withTableName("user")
        .usingGeneratedKeyColumns("id")

    override fun findByLogin(login: Login): Entity.Existing<UserInfo>? = jdbcTemplate.query(
        "select * from user where login=:login",
        mapOf("login" to login.value)){ rs, _ ->
        Entity.Existing(
            Id(rs.getInt("id")),
            UserInfo.of(
                rs.getString("login"),
                rs.getString("firstname"),
                rs.getString("lastname")
            )
        )
    }
    .firstOrNull()

    override fun save(user: Entity<UserInfo>): Entity.Existing<UserInfo> {
        val parameters = with(user.info) {
            mapOf(
                "login" to login.value,
                "firstname" to name.firstname,
                "lastname" to name.lastname
            )
        }
        return when(user){
            is Entity.New ->{
                insertUser
                    .executeAndReturnKey(parameters)
                    .let { id -> Entity.Existing(Id(id.toInt()), user.info) }
            }
            is Entity.Existing -> jdbcTemplate
                .update("update user set login=:login, firstname=:firstname, lastname=:lastname", parameters)
                .let { user }
        }
    }
}