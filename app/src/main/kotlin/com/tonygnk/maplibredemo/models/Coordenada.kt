package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity


@Entity(tableName = "coordenadas")
data class Coordenada(
    @PrimaryKey(autoGenerate = true) val id_coordenada: Int,
    @ColumnInfo(name = "latitud") val lat: Double,
    @ColumnInfo(name = "longitud") val lon: Double,
)