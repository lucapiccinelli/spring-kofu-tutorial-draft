package com.example.repositories

import com.example.model.Entity
import com.example.model.user.Login
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoriesTests {
    private val dataSource: DataSource = JdbcTestsHelper.getDataSource()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val repoHelper = JdbcTestsHelper(jdbcTemplate)

    @BeforeAll
    internal fun setUpAll() {
        repoHelper.createUserTable()
        repoHelper.createArticleTable()
        repoHelper.insertArticle(JdbcTestsHelper.article1)
    }

    @AfterAll
    internal fun tearDownAll() {
        repoHelper.dropArticleTable()
        repoHelper.dropUserTable()
    }

    @Test
    fun `When findByLogin then return User`() {
        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val user = userRepository.findByLogin(JdbcTestsHelper.luca.login)
        user?.info shouldBe JdbcTestsHelper.luca
    }

    @Test
    fun `insert user`() {
        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val newUser = Entity.New(JdbcTestsHelper.luca.copy(login = Login("banana")))
        val user = userRepository.save(newUser)

        user.info shouldBe newUser.info
    }
}

