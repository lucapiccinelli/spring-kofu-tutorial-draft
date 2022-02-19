package com.example.routes

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.User

data class RenderedUser(
    val id: Int,
    val login: String,
    val firstname: String,
    val lastname: String,
    val description: String?)

fun Entity.Existing<User>.render() = RenderedUser(
    id.value,
    info.login.value,
    info.name.firstname,
    info.name.lastname,
    info.description)
