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
    private val repo: RutaRepository,
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
}

