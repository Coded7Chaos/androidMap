package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {
    @Query("Select * from rutas")
    fun getAllRutas(): Flow<List<Ruta>>

    @Query("Select * from rutas where id_ruta_puma=:id_ruta_puma")
    fun getRutaById(id_ruta_puma: Int): Flow<Ruta>

    @Insert
    suspend fun insert(ruta: Ruta)

    @Update
    suspend fun update(ruta: Ruta)

    @Query("Delete from rutas where id_ruta_puma=:id_ruta_puma")
    fun deleteRuta(id_ruta_puma: Int)
}