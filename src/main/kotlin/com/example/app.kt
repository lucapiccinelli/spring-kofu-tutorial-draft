package com.example

import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerResponse

val app = webApplication {
    webMvc {
        mustache()
        router {
            GET("/"){ ServerResponse.ok().render("blog", mapOf("title" to "my blog")) }
        }
    }
}

fun main(args: Array<String>){
    app.run(args)
}