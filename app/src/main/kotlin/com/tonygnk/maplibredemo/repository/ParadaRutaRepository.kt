package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import kotlinx.coroutines.flow.Flow

interface ParadaRutaRepository
{
    fun getIdRutaStream(id_parada: Int): Flow<List<Int>>

    fun getParadas(id_ruta: Int): Flow<List<Int>>

    fun getParadaRutaInfo(id_parada: Int, id_ruta: Int): Flow<ParadaRuta>
}