package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.data.ParadaRutaDao
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import kotlinx.coroutines.flow.Flow

class OfflineParadaRutaRepository(
    private val paradaRutaDao: ParadaRutaDao
): ParadaRutaRepository {
    override fun getIdRutaStream(id_parada: Int): Flow<List<Int>> = paradaRutaDao.getIdRuta(id_parada)

    override fun getParadas(id_ruta: Int): Flow<List<Int>> = paradaRutaDao.getParadas((id_ruta))

    override fun getParadaRutaInfo(
        id_parada: Int,
        id_ruta: Int
    ): Flow<ParadaRuta> = paradaRutaDao.getParadaRutaInfo(id_parada, id_ruta)
}