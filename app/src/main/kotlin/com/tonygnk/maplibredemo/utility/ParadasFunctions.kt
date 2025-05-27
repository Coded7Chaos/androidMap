package com.tonygnk.maplibredemo.utility

import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParadasFunctions(
    private val paradaRepository: ParadaRepository
) {
    fun paradas_cercanas(lat: Double, lon: Double): Flow<Paradas>
    {
        return paradaRepository.getAllParadasStream()
            .map { listaParadas ->
                val filtradas = listaParadas.filter { parada ->
                    distancia(lat, lon, parada.lat, parada.lon) < 600
                }
                Paradas(filtradas)
            }
    }

    private fun distancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Radio de la Tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}

data class Paradas(val paradas: List<Parada> = listOf())