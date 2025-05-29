package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Ubicacion
import com.tonygnk.maplibredemo.models.UbicacionDao
import kotlinx.coroutines.flow.Flow

class OfflineUbicacionRepository(private val ubicacionDao: UbicacionDao) : UbicacionRepository {
    override fun searchUbicacionByNombre(nombreUbicacion: String): Flow<List<Ubicacion>> = ubicacionDao.buscarPorNombre(nombreUbicacion)
}