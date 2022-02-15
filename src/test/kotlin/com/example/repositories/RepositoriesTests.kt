package com.example.repositories

import com.example.model.Entity
import com.example.model.user.Login
import com.example.model.user.UserInfo
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class RepositoriesTests {
    private val dataSource: DataSource = JdbcTestsHelper.getDataSource()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val repoHelper = JdbcTestsHelper(jdbcTemplate)

    @BeforeEach
    internal fun setUpAll() {
        repoHelper.createUserTable()
        repoHelper.createArticleTable()
        repoHelper.insertArticle(JdbcTestsHelper.article1)
    }

    @AfterEach
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
        val login = getLoginById(user)

        login shouldBe newUser.info.login.value
    }

    @Test
    fun `update user`() {
        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val newUser = Entity.New(JdbcTestsHelper.luca.copy(login = Login("banana 2")))
        val insertedUser = userRepository.save(newUser)

        insertedUser.info shouldBe newUser.info

        val update = insertedUser.copy(info = insertedUser.info.copy(login = Login("banana 3")))
        val updatedUser = userRepository.save(update)

        updatedUser.id shouldBe insertedUser.id
        updatedUser.info shouldBe update.info

        val login = getLoginById(updatedUser)

        login shouldBe updatedUser.info.login.value
    }

    private fun getLoginById(updatedUser: Entity.Existing<UserInfo>) =
        jdbcTemplate
            .query("select * from user where id=${updatedUser.id.value}") { rs, _ -> rs.getString("login") }
            .firstOrNull()
}

