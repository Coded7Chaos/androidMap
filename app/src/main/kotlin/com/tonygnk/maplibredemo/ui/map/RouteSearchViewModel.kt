package com.tonygnk.maplibredemo.ui.map

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.repository.ParadaRelation
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.ParadaRutaDetail
import com.tonygnk.maplibredemo.repository.ParadaRutaRepository
import com.tonygnk.maplibredemo.repository.Paradas
import com.tonygnk.maplibredemo.repository.ParadasFunctions
import com.tonygnk.maplibredemo.repository.RutaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import org.maplibre.android.geometry.LatLng
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flowOf

class RouteSearchViewModel(
    val repo: RutaRepository,
    private val paradaRutaRepository: ParadaRutaRepository,
    private val paradaRepository: ParadaRepository,

    ): ViewModel() {
    private val helper = ParadasFunctions(paradaRepository, paradaRutaRepository)

    // 1) Origen y destino (setean cuando pulsas “Buscar ruta”)
    private val _origin     = MutableStateFlow<LatLng?>(null)
    private val _destination= MutableStateFlow<LatLng?>(null)

    fun setPoints(origin: LatLng, destination: LatLng) {
        _origin.value = origin
        _destination.value = destination
    }

    // 2) Flujo de ParadaRelation = combina paradas cercanas + relaciona
    val routeCandidates: StateFlow<List<ParadaRelation>> =
        combine(
            _origin.filterNotNull(),
            _destination.filterNotNull()
        ) { origin, dest ->
            origin to dest
        }
            .flatMapLatest { (origin, dest) ->
                // saco paradas cercanas en ambos puntos
                flow {
                    val pa = helper.paradas_cercanas(origin.latitude, origin.longitude).first()
                    val pb = helper.paradas_cercanas(dest.latitude, dest.longitude).first()
                    emit(helper.relacionar(pa, pb))
                }
            }
            .stateIn(
                scope         = viewModelScope,
                started       = SharingStarted.WhileSubscribed(5_000),
                initialValue  = emptyList()
            )

    // 3) Flujo de pares detallados (ParadaRutaDetail) filtrando por orden
    val detailPairs: StateFlow<List<Pair<ParadaRutaDetail, ParadaRutaDetail>>> =
        routeCandidates
            .flatMapLatest { relations ->
                flow {
                    // extrae ParadaRutaDetail combinados
                    val list = helper.extractCoordenadaPairs(relations)
                    emit(list)
                }
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        // Loguear candidatos crudos
        viewModelScope.launch {
            routeCandidates.collect { list ->
                Log.d(TAG, "routeCandidates: $list")
            }
        }
        // Loguear pares detallados
        viewModelScope.launch {
            detailPairs.collect { pairs ->
                Log.d(TAG, "detailPairs: $pairs")
            }
        }
    }

    val filteredPairs: StateFlow<List<Pair<ParadaRutaDetail, ParadaRutaDetail>>> =
        combine(
            detailPairs,               // Lista de todos los pares detallados
            _origin.filterNotNull(),   // Origen actual
            _destination.filterNotNull() // Destino actual
        ) { allPairs, origin, dest ->
            // 1) Si no hay origen/destino o no hay pares, devolvemos vacío:
            if (allPairs.isEmpty()) return@combine emptyList()

            // 2) Agrupamos todos los pares por idRuta:
            val grouped: Map<Int, List<Pair<ParadaRutaDetail, ParadaRutaDetail>>> =
                allPairs.groupBy { it.first.idRuta }

            // 3) Para cada grupo, buscamos el par de menor “score”:
            val bestPerRoute = mutableListOf<Pair<ParadaRutaDetail, ParadaRutaDetail>>()
            for ((rutaId, pares) in grouped) {
                var bestPair: Pair<ParadaRutaDetail, ParadaRutaDetail>? = null
                var bestScore = Double.MAX_VALUE
                for ((a, b) in pares) {
                    val distA = helper.distancia(origin.latitude, origin.longitude, a.lat, a.lon)
                    val distB = helper.distancia(dest.latitude, dest.longitude, b.lat, b.lon)
                    val score = distA + distB

                    if (score < bestScore) {
                        bestScore = score
                        bestPair = (a to b)
                    }
                }
                bestPair?.let { bestPerRoute.add(it) }
            }
            bestPerRoute
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    init {
        viewModelScope.launch {
            filteredPairs.collect { list ->
                Log.d(TAG, "filteredPairs (mejor por ruta): $list")
            }
        }
    }

    val detailsWithRuta: StateFlow<List<DetailWithRuta>> =
        filteredPairs
            .flatMapLatest { paresFiltrados ->
                flow {
                    // Para cada par, buscamos el nombre de la ruta de forma secuencial
                    val listaConNombres = paresFiltrados.map { (a, b) ->
                        // NOTE: suponemos que getRutaNombre es suspend y devuelve el String de inmediato
                        val nombre = repo.getRutaNombre(a.idRuta)
                        DetailWithRuta(
                            detalleA = a,
                            detalleB = b,
                            nombreRuta = nombre
                        )
                    }
                    emit(listaConNombres)
                }
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    init {
        viewModelScope.launch {
            // Solo para debug: ver los tripletes con nombre en Logcat
            detailsWithRuta.collect { lista ->
                Log.d(TAG, "detailsWithRuta: $lista")
            }
        }
    }
}

data class DetailWithRuta(
    val detalleA: ParadaRutaDetail,
    val detalleB: ParadaRutaDetail,
    val nombreRuta: String
)

