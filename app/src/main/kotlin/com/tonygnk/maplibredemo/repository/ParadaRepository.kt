package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.flow.Flow

interface ParadaRepository {
    fun getAllParadasStream(): Flow<List<Parada>>

    fun getParadaStream(id: Int): Flow<Parada?>

    suspend fun insertParada(parada: Parada)

    suspend fun deleteParada(parada: Parada)

    suspend fun updateParada(parada: Parada)

    fun searchParadaByNombre(nombreDeParada: String): Flow<List<Parada>>
}