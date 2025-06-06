package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Query
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import kotlinx.coroutines.flow.Flow

@Dao
interface ParadaRutaDao
{
    @Query("SELECT id_ruta FROM paradaruta WHERE id_parada = :id_parada")
    fun getIdRuta(id_parada: Int): Flow<List<Int>>

    @Query("SELECT pr.id_parada FROM paradaruta pr JOIN paradas p ON p.id_parada = pr.id_parada WHERE id_ruta = :id_ruta AND p.estado=1")
    fun getParadas(id_ruta: Int): Flow<List<Int>>

    @Query("SELECT * FROM paradaruta WHERE id_parada = :id_parada AND id_ruta=:id_ruta")
    fun getParadaRutaInfo(id_parada: Int, id_ruta: Int): Flow<ParadaRuta>
}