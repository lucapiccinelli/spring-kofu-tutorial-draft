package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.Login
import com.example.model.user.User
import io.kotest.matchers.shouldBe
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class RepositoriesTests {
    private val dataSource: DataSource = JdbcTestsHelper.getDataSource()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val repoHelper = JdbcTestsHelper(dataSource)
    private lateinit var articleId: Number
    private lateinit var userId: Number

    @BeforeEach
    internal fun setUp() {
        val liquibase = Liquibase(
            "classpath:liquibase/changelog-master.xml",
            ClassLoaderResourceAccessor(),
            JdbcConnection(dataSource.connection)
        )
        liquibase.update("")

        repoHelper.insertArticle(JdbcTestsHelper.article1).let { (user, article) ->
            userId = user
            articleId = article
        }
    }

    @AfterEach
    internal fun tearDown() {
        repoHelper.dropDb()
    }

    @Test
    fun `When findByLogin then return User`() {
        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val user = userRepository.findByLogin(JdbcTestsHelper.luca.login)
        user?.info shouldBe JdbcTestsHelper.luca
    }

    @Test
    fun `When findByIdOrNull then return User`() {
        val userRepository = JdbcUserRepositoryImpl(dataSource)
        val user = userRepository.findByIdOrNull(Id(userId.toInt()))
        user?.info shouldBe JdbcTestsHelper.luca
    }

    @Test
    fun `When findAllByOrderByAddedAtDesc then return a list of Articles ordered by date`() {
        val articleRepository = JdbcArticleRepositoryImpl(dataSource)
        val articles = articleRepository.findAllByOrderByAddedAtDesc()
        articles.map { it.info.title } shouldBe listOf(JdbcTestsHelper.article1.title)
    }

    @Test
    fun `When findByIdOrNull then return Article`() {
        val articleRepository = JdbcArticleRepositoryImpl(dataSource)
        val found = articleRepository.findByIdOrNull(Id(articleId.toInt()))

        found?.info?.title shouldBe JdbcTestsHelper.article1.title
        found?.info?.addedAt shouldBe JdbcTestsHelper.article1.addedAt
        found?.info?.user?.info?.login shouldBe JdbcTestsHelper.article1.user.info.login
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

    private fun getLoginById(updatedUser: Entity.Existing<User>) =
        jdbcTemplate
            .query("select * from user where id=${updatedUser.id.value}") { rs, _ -> rs.getString("login") }
            .firstOrNull()
}
