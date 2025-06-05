package com.tonygnk.maplibredemo.ui.map

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.repository.RutaRepository
import com.tonygnk.maplibredemo.ui.rutasPuma.PuntosRuta
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaDetailDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaViewModel.Companion.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.CameraPosition

class RouteDetailViewModel(
    private val repo: RutaRepository,
    private val handle: SavedStateHandle
): ViewModel() {

    val idInicio: Int = checkNotNull(handle[RouteDetailDestination.idInicio])
    val idFin: Int = checkNotNull(handle[RouteDetailDestination.idFinal])

    val puntosRuta: StateFlow<PuntosRuta> = repo.getCoordenadasRuta(idInicio, idFin).map { PuntosRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PuntosRuta()
        )
    init{
        viewModelScope.launch {
            puntosRuta.collect{ puntos ->
                if(puntos.puntosList.isNotEmpty()){
                    Log.d(TAG, "Punto encontrado de la ruta: ${puntos.puntosList.first()}")
                }
            }
        }
    }
}