package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.data.UserDao
import com.tonygnk.maplibredemo.models.User
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(private val userDao: UserDao): UserRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserStream(email: String): Flow<User> = userDao.getUserByEmail(email)

    override suspend fun insertUser(user: User) = userDao.insert(user)

    override suspend fun deleteUser(email: String) = userDao.deleteUser(email)

    override suspend fun updateUser(user: User) = userDao.update(user)
}