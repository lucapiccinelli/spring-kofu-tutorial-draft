package com.example.routes

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

val blog = configuration {
    beans {
        bean {
            router {
                GET("/"){ ServerResponse.ok().render("blog", mapOf("title" to "my blog")) }
            }
        }
    }
}