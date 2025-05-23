package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paradas")
data class Parada(
    @PrimaryKey(autoGenerate = true) val id_parada: Int,
    @ColumnInfo(name = "latitud") val lat: Double?,
    @ColumnInfo(name = "longitud") val lon: Double?,
    @ColumnInfo(name = "nombre") val nombre: String?,
    @ColumnInfo(name = "direccion") val direccion: String?,
    @ColumnInfo(name = "estado") val estado: Boolean?
)