package com.example.routes

import com.example.model.Entity
import com.example.model.Id
import com.example.model.article.Article
import com.example.model.user.Login
import com.example.model.user.User
import com.example.mustache
import com.example.repositories.ArticleEntity
import com.example.repositories.ArticleRepository
import com.example.repositories.UserRepository
import com.example.toSlug
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.webApplication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

object RandomServerPort {
    fun value(): Int = (10000..10500).random()
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogTests {

    lateinit var context: ConfigurableApplicationContext
    lateinit var client: MockMvc

    private val app = webApplication {
        enable(mustache)
        enable(blog)
        beans {
            val luca = Entity.Existing(Id(0), User.of("springluca", "Luca", "Piccinelli"))
            val articles = listOf(
                ArticleEntity(Id(0),
                    Article(
                        title = "Reactor Bismuth is out",
                        headline = "Lorem ipsum",
                        content = "dolor sit amet",
                        userFn = { luca }
                    )
                ),
                ArticleEntity(Id(1),
                    Article(
                        title = "Reactor Aluminium has landed",
                        headline = "Lorem ipsum",
                        content = "dolor sit amet",
                        userFn = { luca }
                    )
                )
            )

            bean {
                mockk<ArticleRepository> {
                    every { findAllByOrderByAddedAtDesc() } returns articles.sortedBy { it.info.addedAt }
                    every { findBySlug(articles.last().info.slug) } returns articles.last()
                }
            }
        }
    }

    @BeforeAll
    internal fun setUp() {
        context = app.run(arrayOf("--server.port=${RandomServerPort.value()}"))
        client = MockMvcBuilders
            .webAppContextSetup(context as WebApplicationContext)
            .build()
    }

    @AfterAll
    internal fun tearDown() {
        context.close()
    }

    @Test
    internal fun `Assert blog page title, content and status code`() {
        client.get("/")
            .andExpect {
                status { isOk() }
                content {
                    string(Matchers.containsString("<h1>Blog</h1>"))
                    string(Matchers.containsString("Reactor"))
                }
            }
    }

    @Test
    internal fun `Assert article page title, content and status code`() {
        val title = "Reactor Aluminium has landed"

        client.get("/article/${title.toSlug()}")
            .andExpect {
                status { isOk() }
                content {
                    string(Matchers.containsString(title))
                    string(Matchers.containsString("Lorem ipsum"))
                    string(Matchers.containsString("dolor sit amet"))
                }
            }
    }
}