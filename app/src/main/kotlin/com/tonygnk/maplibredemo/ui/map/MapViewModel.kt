package com.tonygnk.maplibredemo.ui.map

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.ui.home.HomeUiState
import com.tonygnk.maplibredemo.ui.parada.ParadaDatos
import com.tonygnk.maplibredemo.ui.parada.ParadaUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn


data class ResultadosQuery( val nombresParadas: List<String> = listOf())

class MapViewModel( private val paradaRepository: ParadaRepository ): ViewModel() {

    var searchValue by mutableStateOf("")
        private set

    val resultadosQuery: StateFlow<ResultadosQuery> = snapshotFlow { searchValue }
        .debounce(600)
        .flatMapLatest { query ->
            if(query.isBlank()){
                flowOf(ResultadosQuery())
            } else{
                paradaRepository.searchParadaByNombre(query)
                    .map{ ResultadosQuery(it) }
                    .catch { emit(ResultadosQuery()) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ResultadosQuery()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    fun updateSearchValue(newValue:String){
        searchValue = newValue
    }

}


