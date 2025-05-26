package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonygnk.maplibredemo.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("Select * from usuario")
    fun getAllUsers(): Flow<List<User>>

    @Query("Select * from usuario where email=:email")
    fun getUserByEmail(email: String): Flow<User>

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("Delete from usuario where email=:email")
    fun deleteUser(email: String)
}