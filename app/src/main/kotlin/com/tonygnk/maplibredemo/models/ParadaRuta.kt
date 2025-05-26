package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "paradaruta",
    primaryKeys = ["id_ruta", "id_parada"],
    foreignKeys = [
        ForeignKey(
            entity = Ruta::class,
            parentColumns = ["id_ruta_puma"],
            childColumns = ["id_ruta"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Parada::class,
            parentColumns = ["id_parada"],
            childColumns = ["id_parada"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Coordenada::class,
            parentColumns = ["id_coordenada"],
            childColumns = ["id_coordenada"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ParadaRuta(
    @ColumnInfo(name = "id_ruta")
    val id_ruta: Int,
    @ColumnInfo(name = "id_parada")
    val id_parada: Int,
    @ColumnInfo(name = "orden")
    val orden: Int,
    @ColumnInfo(name = "tiempo")
    val tiempo: Int,
    @ColumnInfo(name = "id_coordenada")
    val id_coordenada: Int,
)
