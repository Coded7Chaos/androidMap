package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "dia_horario",
    primaryKeys = ["id_horario", "dia_id"],
    foreignKeys = [
        ForeignKey(
            entity = Horario::class,
            parentColumns = ["id_horario"],
            childColumns = ["id_horario"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE // <-- Cambiado a CASCADE
        ),
        ForeignKey(
            entity = Dia::class,
            parentColumns = ["dia_id"],
            childColumns = ["dia_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE // <-- Cambiado a CASCADE
        )
    ]
)
data class DiaHorario(
    @ColumnInfo(name = "id_horario")
    val idHorario: Int,
    @ColumnInfo(name = "dia_id")
    val diaId: Int
)
