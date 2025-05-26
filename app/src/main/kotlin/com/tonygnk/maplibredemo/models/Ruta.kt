package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutas")

data class Ruta (
    @PrimaryKey(autoGenerate = true) val id_ruta_puma: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "sentido") val sentido: String,
    @ColumnInfo(name = "estado") val estado: Boolean
)