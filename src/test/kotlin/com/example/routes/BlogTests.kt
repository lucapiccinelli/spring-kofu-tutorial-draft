package com.example.routes

import com.example.app
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
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
                    string(Matchers.containsString("<h1>my blog</h1>"))
                }
            }
    }
}