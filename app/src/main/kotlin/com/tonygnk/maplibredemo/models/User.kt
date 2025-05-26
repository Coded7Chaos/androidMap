package com.tonygnk.maplibredemo.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "usuario")
data class User(
    @PrimaryKey val email: String,
    val nombre: String,
    val contrasenia: Int,
    val edad: Int
)