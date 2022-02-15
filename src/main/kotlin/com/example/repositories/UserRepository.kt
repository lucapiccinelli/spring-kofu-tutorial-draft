package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.Login
import com.example.model.user.UserInfo

interface UserRepository {
    fun findByLogin(login: Login): Entity.Existing<UserInfo>?
    fun save(user: Entity<UserInfo>): Entity.Existing<UserInfo>
}