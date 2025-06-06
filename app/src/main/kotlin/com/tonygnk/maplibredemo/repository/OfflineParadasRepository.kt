package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.data.ParadaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflineParadasRepository(private val paradaDao: ParadaDao) : ParadaRepository {
    override fun getAllParadasStream(): Flow<List<Parada>> = paradaDao.getAllItems()

    override fun getParadaStream(id: Int): Flow<Parada> = paradaDao.getItem(id)

    override suspend fun insertParada(parada: Parada) = paradaDao.insert(parada)

    override suspend fun deleteParada(parada: Parada) = paradaDao.delete(parada)

    override suspend fun updateParada(parada: Parada) = paradaDao.update(parada)

    override fun searchParadaByNombre(nombreDeParada: String): Flow<List<Parada>> = paradaDao.busquedaNombresParadas(nombreDeParada)

    override suspend fun setParadaInactiva(id_parada: Int) = withContext(Dispatchers.IO){
        paradaDao.setEstadoParada(id_parada, 0)
    }
    override suspend fun setParadaActiva(id_parada: Int) =  withContext(Dispatchers.IO){
        paradaDao.setEstadoParada(id_parada, 1)
    }
}