package com.instant.mviwithouthilt.repository

import com.instant.mviwithouthilt.model.User
import kotlinx.coroutines.delay

// A simple user repository implementation
class UserRepositoryImpl : UserRepository {
    private var users = mutableListOf<User>()

    override suspend fun getUsers(): List<User> {
        delay(1000)
        return users
    }

    override suspend fun addUser(user: User): List<User> {
        delay(500)
        users.add(user)
        return users
    }

    override suspend fun deleteUser(user: User): List<User> {
        delay(500)
        users.remove(user)
        return users
    }

    override suspend fun clearUsers(): List<User> {
        delay(500)
        users.clear()
        return users
    }
}