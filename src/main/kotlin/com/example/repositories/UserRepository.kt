package com.example.repositories

import com.example.model.Entity
import com.example.model.Id
import com.example.model.user.Login
import com.example.model.user.User

interface UserRepository {
    fun findByLogin(login: Login): Entity.Existing<User>?
    fun save(user: Entity<User>): Entity.Existing<User>
    fun findByIdOrNull(id: Id<Int>): Entity.Existing<User>?
    fun findAll(): Collection<Entity.Existing<User>>
}