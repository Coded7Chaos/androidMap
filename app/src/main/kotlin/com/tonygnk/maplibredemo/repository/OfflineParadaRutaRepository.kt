package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.data.ParadaRutaDao
import com.tonygnk.maplibredemo.models.User
import kotlinx.coroutines.flow.Flow

class OfflineParadaRutaRepository(private val paradaRutaDao: ParadaRutaDao): ParadaRutaRepository
{
    override fun getIdRutaStream(id_parada: Int): Flow<List<Int>> = paradaRutaDao.getIdRuta(id_parada)

}