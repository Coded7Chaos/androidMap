package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.repository.RutaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RutasPumaListViewModel(rutaRepository: RutaRepository): ViewModel() {
    val rutaUiState: StateFlow<RutaUiState> = rutaRepository.getAllRutasStream().map { RutaUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = RutaUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RutaUiState(val rutasList: List<Ruta> = listOf())