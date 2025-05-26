package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horario")
data class Horario(
    @PrimaryKey(autoGenerate = true) val id_horario: Int,
    @ColumnInfo(name = "hora_inicio") val horaInicio: Int,
    @ColumnInfo(name = "hora_final") val horaFin: Int
)
