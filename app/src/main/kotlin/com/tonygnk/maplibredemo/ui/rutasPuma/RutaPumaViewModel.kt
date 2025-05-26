package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.RutaRepository
import com.tonygnk.maplibredemo.ui.home.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RutaPumaViewModel(
    private val rutaRepository: RutaRepository,
    savedStateHandle: SavedStateHandle
    ) : ViewModel(){

        private val rutaId: Int = checkNotNull(savedStateHandle[RutaPumaDestination.rutaIdArg])
        private val rutaNombre: String = checkNotNull(savedStateHandle[RutaPumaDestination.rutaNombre])

    val puntosRuta: StateFlow<PuntosRuta> = rutaRepository.getRutaPoints(rutaId).map { PuntosRuta(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PuntosRuta()
        )
        companion object{
            private const val TIMEOUT_MILLIS = 5_000L
        }

    }

data class PuntosRuta(val puntosList: List<Coordenada> = listOf())