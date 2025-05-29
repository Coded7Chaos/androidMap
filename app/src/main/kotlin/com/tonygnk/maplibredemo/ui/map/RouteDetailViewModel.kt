package com.tonygnk.maplibredemo.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.repository.RutaRepository

class RouteDetailViewModel(
    private val repo: RutaRepository,
    private val handle: SavedStateHandle
): ViewModel() {
    val routeId: Int = handle.get<Int>("routeId")!!
    suspend fun loadDetail(): Ruta {
        return repo.getRutaStream(routeId)
    }
}