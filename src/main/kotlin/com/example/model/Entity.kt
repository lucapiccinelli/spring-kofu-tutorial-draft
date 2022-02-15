package com.example.model

sealed class Entity<out T>(open val info: T){
    data class Existing<out T>(val id: Id<Int>, override val info: T) : Entity<T>(info)
    data class New<out T>(override val info: T) : Entity<T>(info)
}
