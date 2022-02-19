package com.example.routes

import com.example.mustache
import com.example.properties.BlogProperties
import com.example.repositories.ArticleRepository
import com.example.toSlug
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.KofuApplication
import org.springframework.fu.kofu.webApplication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogTests {

    lateinit var context: ConfigurableApplicationContext
    lateinit var client: MockMvc

    private val app: KofuApplication = webApplication {
        enable(mustache)
        enable(blog)
        beans {
            val articles = TestsEntitiesHelper.articles
            bean {
                mockk<ArticleRepository> {
                    every { findAllByOrderByAddedAtDesc() } returns articles.sortedBy { it.info.addedAt }
                    every { findBySlug(articles.last().info.slug) } returns articles.last()
                }
            }

            bean { BlogProperties("Blog", BlogProperties.Banner("bla", "bla")) }
        }
    }

    @BeforeAll
    internal fun setUp() {
        context = RandomServerPort.start(app)
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
