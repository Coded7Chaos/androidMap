package com.tonygnk.maplibredemo.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "ubicaciones")
data class Ubicacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "latitud") val latitud: Double,
    @ColumnInfo(name = "longitud") val longitud: Double
)

@Dao
interface UbicacionDao {
    @Insert
    suspend fun insertar(ubicacion: Ubicacion)

    @Query("SELECT * FROM ubicaciones WHERE nombre LIKE '%' || :query || '%'")
    fun buscarPorNombre(query: String): Flow<List<Ubicacion>>

    @Query("SELECT * FROM ubicaciones")
    suspend fun obtenerTodos(): List<Ubicacion>
}