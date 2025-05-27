package com.tonygnk.maplibredemo.utility

import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.ParadaRutaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ParadasFunctions(
    private val paradaRepository: ParadaRepository,
    private val paradaRutaRepository: ParadaRutaRepository
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

    private fun distancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double
    {
        val R = 6371000.0 // Radio de la Tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    suspend fun relacionar(
        conjuntoA: Paradas,
        conjuntoB: Paradas
    ): List<Pair<Parada, Parada>> = coroutineScope {
        val rutasADeferred = conjuntoA.paradas.map { parada ->
            async {
                parada to paradaRutaRepository
                    .getIdRutaStream(parada.id_parada)
                    .first()
            }
        }

        val rutasBDeferred = conjuntoB.paradas.map { parada ->
            async {
                parada to paradaRutaRepository
                    .getIdRutaStream(parada.id_parada)
                    .first()
            }
        }

        val rutasA: Map<Parada, List<Int>> = rutasADeferred.awaitAll().toMap()
        val rutasB: Map<Parada, List<Int>> = rutasBDeferred.awaitAll().toMap()

        val relaciones = mutableListOf<Pair<Parada, Parada>>()
        for (pA in conjuntoA.paradas)
        {
            val rutasDeA = rutasA[pA].orEmpty()
            for (pB in conjuntoB.paradas)
            {
                val rutasDeB = rutasB[pB].orEmpty()
                if (rutasDeA.any { it in rutasDeB })
                {
                    relaciones += pA to pB
                }
            }
        }
        relaciones
    }
}

data class Paradas(val paradas: List<Parada> = listOf())