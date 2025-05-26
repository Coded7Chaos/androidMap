package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.data.RutaDao
import com.tonygnk.maplibredemo.models.Ruta
import kotlinx.coroutines.flow.Flow

class OfflineRutaRepository(private val rutaDao: RutaDao): RutaRepository {
    override fun getAllRutasStream(): Flow<List<Ruta>> = rutaDao.getAllRutas()

    override fun getRutaStream(id_ruta_puma: Int): Flow<Ruta> = rutaDao.getRutaById(id_ruta_puma)

    override suspend fun insertRuta(ruta: Ruta) = rutaDao.insert(ruta)

    override suspend fun deleteRuta(id_ruta_puma: Int) = rutaDao.deleteRuta(id_ruta_puma)

    override suspend fun updateRuta(ruta: Ruta) = rutaDao.update(ruta)
}