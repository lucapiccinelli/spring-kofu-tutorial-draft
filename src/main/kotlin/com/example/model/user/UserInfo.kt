package com.example.model.user

import com.example.model.Name

data class UserInfo(
    val login: Login,
    val name: Name,
    val description: String? = null){

    companion object{
        fun of(
            login: String,
            firstname: String,
            lastname: String,
            description: String? = null) =
            UserInfo(Login(login), Name(firstname, lastname), description)
    }
}

