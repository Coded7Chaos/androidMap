package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

import androidx.room.Query
import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.flow.Flow

@Dao
interface ParadaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(parada: Parada)
    @Update
    suspend fun update(parada: Parada)
    @Delete
    suspend fun delete(parada: Parada)

    @Query("SELECT * from paradas WHERE id_parada = :id")
    fun getItem(id: Int): Flow<Parada>

    @Query("SELECT * FROM paradas WHERE estado=1 ORDER BY nombre ASC ")
    fun getAllItems(): Flow<List<Parada>>

    @Query("SELECT * FROM paradas WHERE nombre LIKE '%' || :nombreDeParada || '%'")
    fun busquedaNombresParadas(nombreDeParada: String): Flow<List<Parada>>

    @Query("UPDATE paradas SET estado=:estado WHERE id_parada=:idParada")
    suspend fun setEstadoParada(idParada: Int, estado: Int)
}