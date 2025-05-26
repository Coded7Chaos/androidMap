package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Ruta
import kotlinx.coroutines.flow.Flow

interface RutaRepository {
    fun getAllRutasStream(): Flow<List<Ruta>>

    fun getRutaStream(id_ruta_puma: Int): Flow<Ruta>

    suspend fun insertRuta(ruta: Ruta)

    suspend fun deleteRuta(id_ruta_puma: Int)

    suspend fun updateRuta(ruta: Ruta)

    fun getRutaPoints(id_ruta_puma: Int): Flow<List<Coordenada>>
}