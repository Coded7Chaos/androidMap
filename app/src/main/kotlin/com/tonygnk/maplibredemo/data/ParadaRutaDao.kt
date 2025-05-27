package com.tonygnk.maplibredemo.data

import androidx.room.Dao
import androidx.room.Query
import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.flow.Flow

@Dao
interface ParadaRutaDao
{
    @Query("SELECT id_ruta from paradaruta WHERE id_parada = :id_parada")
    fun getIdRuta(id_parada: Int): Flow<List<Int>>
}