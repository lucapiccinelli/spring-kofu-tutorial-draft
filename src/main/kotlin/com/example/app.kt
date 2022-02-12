package com.example

import com.example.routes.blog
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerResponse

val app = webApplication {
    enable(blog)
    webMvc {
        mustache()
    }
}

fun main(args: Array<String>){
    app.run(args)
}