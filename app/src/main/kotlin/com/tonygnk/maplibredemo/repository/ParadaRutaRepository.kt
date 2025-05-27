package com.tonygnk.maplibredemo.repository

import kotlinx.coroutines.flow.Flow

interface ParadaRutaRepository
{
    fun getIdRutaStream(id_parada: Int): Flow<List<Int>>
}