package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paradas")
data class Parada(
    @PrimaryKey(autoGenerate = true) val id_parada: Int = 0,
    @ColumnInfo(name = "latitud") val lat: Double = 0.0,
    @ColumnInfo(name = "longitud") val lon: Double = 0.0,
    @ColumnInfo(name = "nombre") val nombre: String = "",
    @ColumnInfo(name = "direccion") val direccion: String = "",
    @ColumnInfo(name = "estado") val estado: Boolean = true
)