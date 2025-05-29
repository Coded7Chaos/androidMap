package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import kotlinx.coroutines.flow.Flow

interface ParadaRepository {
    fun getAllParadasStream(): Flow<List<Parada>>

    fun getParadaStream(id_parada: Int): Flow<Parada>

    suspend fun insertParada(parada: Parada)

    suspend fun deleteParada(parada: Parada)

    suspend fun updateParada(parada: Parada)

    fun searchParadaByNombre(nombreDeParada: String): Flow<List<Parada>>

}