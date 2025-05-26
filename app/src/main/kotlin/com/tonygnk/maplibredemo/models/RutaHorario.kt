package com.tonygnk.maplibredemo.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "ruta_horario",
    primaryKeys = ["id_ruta_puma", "id_horario"],
    foreignKeys = [
        ForeignKey(
            entity = Horario::class,
            parentColumns = ["id_horario"],
            childColumns = ["id_horario"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ruta::class,
            parentColumns = ["id_ruta_puma"],
            childColumns = ["id_ruta_puma"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RutaHorario(
    val id_ruta_puma: Int,
    val id_horario: Int
)
