package com.example

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerResponse

val app = webApplication {
    webMvc {
        router {
            GET("/test"){ ServerResponse.ok().body("ok") }
        }
    }
}

fun main(args: Array<String>){
    app.run(args)
}