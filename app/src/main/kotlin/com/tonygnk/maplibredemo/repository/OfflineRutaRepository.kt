package com.tonygnk.maplibredemo.repository

import android.util.Log
import com.tonygnk.maplibredemo.data.RutaDao
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Ruta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class OfflineRutaRepository(private val rutaDao: RutaDao): RutaRepository {
    override fun getAllRutasStream(): Flow<List<Ruta>> = rutaDao.getAllRutas()

    override fun getRutaStream(id_ruta_puma: Int): Flow<Ruta> = rutaDao.getRutaById(id_ruta_puma)

    override suspend fun insertRuta(ruta: Ruta) = rutaDao.insert(ruta)

    override suspend fun deleteRuta(id_ruta_puma: Int) = rutaDao.deleteRuta(id_ruta_puma)

    override suspend fun updateRuta(ruta: Ruta) = rutaDao.update(ruta)

    override fun getRutaPoints(id_ruta_puma: Int): Flow<List<Coordenada>> {
        return rutaDao.getParadasConOrden(id_ruta_puma)
            .catch { e ->
                emit(emptyList())
                Log.e("RutaRepository", "Error al obtener puntos", e)
            }
            .filter { paradas -> paradas.isNotEmpty() } // Usa parámetro explícito
            .flatMapLatest { paradas ->
                val coordenadaA = paradas.first { it.orden == 1 }.id_coordenada
                val maxOrden = paradas.maxByOrNull { it.orden }?.orden ?: 1
                val coordenadaB = paradas.first { it.orden == maxOrden }.id_coordenada
                rutaDao.getCoordenadasRuta(coordenadaA, coordenadaB)
            }
    }

}