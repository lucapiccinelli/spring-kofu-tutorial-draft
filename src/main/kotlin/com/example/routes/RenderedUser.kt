package com.example.routes

import com.example.model.user.User

data class RenderedUser(
    val login: String,
    val firstname: String,
    val lastname: String,
    val description: String?)

fun User.render() = RenderedUser(login.value, name.firstname, name.lastname, description)
