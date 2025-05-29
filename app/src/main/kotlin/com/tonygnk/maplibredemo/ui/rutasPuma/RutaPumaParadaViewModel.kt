package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.ParadaRutaRepository
import com.tonygnk.maplibredemo.repository.RutaRepository
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaViewModel.Companion.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RutaPumaParadaViewModel(
    rutaRepository: RutaRepository,
    paradaRepository: ParadaRepository,
    paradaRutaRepository: ParadaRutaRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val rutaId: Int = checkNotNull(savedStateHandle[RutaPumaDestination.rutaIdArg])
    private val paradaId: Int = checkNotNull(savedStateHandle[RutaPumaParadaDestination.paradaIdArg])

    val puntosRuta: StateFlow<PuntosRuta> = rutaRepository.getRutaPoints(rutaId).map { PuntosRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PuntosRuta()
        )

    val paradasRuta: StateFlow<ParadasRuta> = paradaRutaRepository.getParadas(rutaId).map { ParadasRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(RutaPumaViewModel.Companion.TIMEOUT_MILLIS),
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
            started = SharingStarted.WhileSubscribed(RutaPumaViewModel.Companion.TIMEOUT_MILLIS),
            initialValue = emptyList()
        )
    val parada: StateFlow<Parada> = paradaRepository.getParadaStream(paradaId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(RutaPumaViewModel.Companion.TIMEOUT_MILLIS),
            initialValue = Parada()
        )
}