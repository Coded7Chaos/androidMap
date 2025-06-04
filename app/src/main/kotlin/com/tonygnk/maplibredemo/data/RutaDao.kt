package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonygnk.maplibredemo.models.ParadaRutaCoordenada
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.models.User

import com.tonygnk.maplibredemo.models.Coordenada
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

    @Query("SELECT orden, id_coordenada FROM paradaruta WHERE id_ruta = :id_ruta_puma AND orden>0 ORDER BY orden")
    fun getParadasConOrden(id_ruta_puma: Int): Flow<List<ParadaRutaCoordenada>>

    @Query("SELECT * FROM coordenadas WHERE id_coordenada BETWEEN :idInicio AND :idFin ORDER BY id_coordenada")
    fun getCoordenadasRuta(idInicio: Int, idFin: Int): Flow<List<Coordenada>>

    @Query("SELECT nombre FROM rutas WHERE id_ruta_puma=:id_ruta_puma")
    suspend fun getRutaNombre(id_ruta_puma: Int): String
}