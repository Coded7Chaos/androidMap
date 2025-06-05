package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.ParadaRutaRepository
import com.tonygnk.maplibredemo.repository.RutaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.CameraPosition

class RutaPumaViewModel(
    private val rutaRepository: RutaRepository,
    private val paradaRutaRepository: ParadaRutaRepository,
    private val paradaRepository: ParadaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val cameraPositionState = mutableStateOf(
            CameraPosition(
                target = LatLng(-16.4897, -68.1193),
                zoom = 14.0
            )
        )

    fun updateCameraPosition(position: CameraPosition){
        cameraPositionState.value = position
    }



    val rutaId: Int = checkNotNull(savedStateHandle[RutaDetailDestination.rutaIdArg])
    val rutaNombre: String = checkNotNull(savedStateHandle[RutaDetailDestination.rutaNombre])

    val puntosRuta: StateFlow<PuntosRuta> = rutaRepository.getRutaPoints(rutaId).map { PuntosRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PuntosRuta()
        )

    val paradasRuta: StateFlow<ParadasRuta> = paradaRutaRepository.getParadas(rutaId).map { ParadasRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ParadasRuta()
        )

    val detalleParadas: StateFlow<List<Parada>> = paradasRuta.map { it.paradasList }
            .flatMapLatest { listaIds: List<Int> ->
                if (listaIds.isEmpty())
                {
                    flowOf(emptyList())
                }
                else
                {
                    combine(
                        listaIds.map { id ->
                            paradaRepository.getParadaStream(id)
                        }
                    ) { arrayParadas ->
                        // combine nos da un Array<Parada>, convertimos a List
                        arrayParadas.toList()
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )
    companion object
    {
        const val TIMEOUT_MILLIS = 5_000L
    }
    init{
        viewModelScope.launch {
            detalleParadas.collect{ paradas ->
                if(paradas.isNotEmpty()){
                    val primeraParada = paradas.first()
                    updateCameraPosition(
                        CameraPosition(
                            target = LatLng(primeraParada.lat, primeraParada.lon),
                            zoom = 14.0
                        )
                    )
                }
            }
        }
    }
}

data class PuntosRuta(val puntosList: List<Coordenada> = listOf())
data class ParadasRuta(val paradasList: List<Int> = listOf())