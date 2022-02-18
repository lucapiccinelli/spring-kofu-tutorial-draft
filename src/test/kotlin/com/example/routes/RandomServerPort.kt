package com.example.routes

import org.springframework.fu.kofu.KofuApplication

object RandomServerPort {
    fun value(): Int = (10000..10500).random()

    fun start(app: KofuApplication) =
        app.run(arrayOf("--server.port=${value()}"))
}