package com.example.routes

import com.example.json
import com.example.repositories.ArticleRepository
import com.example.repositories.UserRepository
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
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.function.ServerResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTests {
    lateinit var context: ConfigurableApplicationContext
    lateinit var client: MockMvc

    private val app: KofuApplication = webApplication {
        enable(json)
        enable(api)
        beans {
            bean {
                mockk<ArticleRepository> {
                    every { findAll() } returns TestsEntitiesHelper.articles
                }
            }
            bean {
                mockk<UserRepository> {
                    every { findAll() } returns TestsEntitiesHelper.users
                }
            }
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
    internal fun `List articles`() {
        client.get("/api/article/"){ accept(MediaType.APPLICATION_JSON) }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("\$.[0].user.login", Matchers.equalTo(TestsEntitiesHelper.articles[0].info.user.info.login.value))
                    jsonPath("\$.[0].slug", Matchers.equalTo(TestsEntitiesHelper.articles[0].info.slug))
                    jsonPath("\$.[1].user.login", Matchers.equalTo(TestsEntitiesHelper.articles[1].info.user.info.login.value))
                    jsonPath("\$.[1].slug", Matchers.equalTo(TestsEntitiesHelper.articles[1].info.slug))
                }
            }
    }
}