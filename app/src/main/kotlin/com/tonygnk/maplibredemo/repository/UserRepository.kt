package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsersStream(): Flow<List<User>>

    fun getUserStream(email: String): Flow<User>

    suspend fun insertUser(user: User)

    suspend fun deleteUser(email: String)

    suspend fun updateUser(user: User)
}