package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun extractCoordenadaPairs(
        relaciones: List<ParadaRelation>
    ): List<Pair<ParadaRutaDetail, ParadaRutaDetail>> = coroutineScope {
        relaciones.map { rel ->
            async {
                // 1) Obtengo la info ParadaRuta para A y B
                val infoA = paradaRutaRepository
                    .getParadaRutaInfo(rel.paradaA.id_parada, rel.rutaId)
                    .firstOrNull()
                    ?: return@async null
                val infoB = paradaRutaRepository
                    .getParadaRutaInfo(rel.paradaB.id_parada, rel.rutaId)
                    .firstOrNull()
                    ?: return@async null

                if (infoA.orden >= infoB.orden) return@async null

                val detailA = ParadaRutaDetail(
                    idRuta       = infoA.id_ruta,
                    idParada     = infoA.id_parada,
                    orden        = infoA.orden,
                    tiempo       = infoA.tiempo,
                    idCoordenada = infoA.id_coordenada,
                    lat          = rel.paradaA.lat,
                    lon          = rel.paradaA.lon,
                    nombre       = rel.paradaA.nombre,
                    direccion    = rel.paradaA.direccion,
                    estado       = rel.paradaA.estado
                )
                val detailB = ParadaRutaDetail(
                    idRuta       = infoB.id_ruta,
                    idParada     = infoB.id_parada,
                    orden        = infoB.orden,
                    tiempo       = infoB.tiempo,
                    idCoordenada = infoB.id_coordenada,
                    lat          = rel.paradaB.lat,
                    lon          = rel.paradaB.lon,
                    nombre       = rel.paradaB.nombre,
                    direccion    = rel.paradaB.direccion,
                    estado       = rel.paradaB.estado
                )

                detailA to detailB
                }
            }
            .awaitAll()
            .filterNotNull()
    }

    suspend fun relacionar(
        conjuntoA: Paradas,
        conjuntoB: Paradas
    ): List<ParadaRelation> = coroutineScope {
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

        val relaciones = mutableListOf<ParadaRelation>()
        for (pA in conjuntoA.paradas)
        {
            val rutasDeA = rutasA[pA].orEmpty()
            for (pB in conjuntoB.paradas)
            {
                val rutasDeB = rutasB[pB].orEmpty()
                val comunes = rutasDeA.intersect(rutasDeB.toSet())
                for (rutaId in comunes) {
                    relaciones += ParadaRelation(
                        paradaA = pA,
                        paradaB = pB,
                        rutaId   = rutaId
                    )
                }
            }
        }
        relaciones
    }
}

data class ParadaRelation(
    val paradaA: Parada,
    val paradaB: Parada,
    val rutaId:   Int
)

data class ParadaRutaDetail(
    val idRuta:        Int,
    val idParada:      Int,
    val orden:         Int,
    val tiempo:        Int,
    val idCoordenada:  Int,
    // campos de Parada
    val lat:           Double,
    val lon:           Double,
    val nombre:        String,
    val direccion:     String,
    val estado:        Boolean
)

data class Paradas(val paradas: List<Parada> = listOf())