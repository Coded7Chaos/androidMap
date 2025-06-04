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

    val rutasEncontradas: StateFlow<List<Ruta>> =
        detailPairs
            .map { dp ->
                val ids: List<Int> = dp
                    .map { (detalleA, _) -> detalleA.idRuta }
                    .distinct()
                ids
            }
            .flatMapLatest { listaIds ->
                if (listaIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val flujosDeRuta: List<Flow<Ruta>> = listaIds.map { idRuta ->
                        repo.getRutaStream(idRuta)
                    }
                    combine(flujosDeRuta) { arrayRutas ->
                        arrayRutas.toList()
                    }
                }
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        // Loguear rutas encontradas
        viewModelScope.launch {
            rutasEncontradas.collect { list ->
                Log.d(TAG, "Listas encontradas: $list")
            }
        }
    }


    private val _idInicio = MutableStateFlow<Int?>(null)
    private val _idFin    = MutableStateFlow<Int?>(null)

    // b) (opcional) almacenar lat/lon de cada parada, para poder dibujar un marcador
    private val _coordenadaA = MutableStateFlow<LatLng?>(null)
    private val _coordenadaB = MutableStateFlow<LatLng?>(null)


    fun selectPair(pair: Pair<ParadaRutaDetail, ParadaRutaDetail>) {
        _idInicio.value   = pair.first.idCoordenada
        _idFin.value      = pair.second.idCoordenada
        _coordenadaA.value = LatLng(pair.first.lat, pair.first.lon)
        _coordenadaB.value = LatLng(pair.second.lat, pair.second.lon)
    }

    val coordenadaA: StateFlow<LatLng?> = _coordenadaA
    val coordenadaB: StateFlow<LatLng?> = _coordenadaB


    val rutaCoordenadas: StateFlow<List<Coordenada>> =
        combine(
            _idInicio.filterNotNull(),
            _idFin.filterNotNull()
        ) { inicio, fin ->
            inicio to fin
        }
            .flatMapLatest { (inicio, fin) ->
                // repo.getCoordenadasRuta(...) ya devuelve Flow<List<Coordenada>>
                repo.getCoordenadasRuta(inicio, fin)
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

}

