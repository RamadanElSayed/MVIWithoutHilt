package com.instant.mviwithouthilt.repository

import com.instant.mviwithouthilt.model.User


// User repository interface
interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun addUser(user: User): List<User>
    suspend fun deleteUser(user: User): List<User>
    suspend fun clearUsers(): List<User>
}