package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subrutas")
data class Subruta(
    @PrimaryKey(autoGenerate = true) val id_subruta: Int,
    @ColumnInfo(name = "latitud") val lat: String?,
    @ColumnInfo(name = "longitud") val lon: String?,
    @ColumnInfo(name = "nombre") val nombre: String?

)
