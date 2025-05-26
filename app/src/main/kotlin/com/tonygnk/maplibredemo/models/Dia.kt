package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dia")
data class Dia(
    @PrimaryKey val dia_id: Int,
    @ColumnInfo(name = "descripcion") val descripcion: String
)
