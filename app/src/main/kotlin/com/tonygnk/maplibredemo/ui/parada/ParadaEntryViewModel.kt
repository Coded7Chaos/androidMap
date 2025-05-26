package com.tonygnk.maplibredemo.ui.parada

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonygnk.maplibredemo.data.TransporteDatabase
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.repository.ParadaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ParadaEntryViewModel(private val paradaRepository: ParadaRepository) : ViewModel(){
    var paradaUiState by mutableStateOf(ParadaUiState())
        private set
    fun updateUiState(paradaDatos: ParadaDatos){
        paradaUiState =
            ParadaUiState(paradaDatos = paradaDatos, entradaValida = validarEntrada(paradaDatos))
    }

    private fun validarEntrada(uiState: ParadaDatos = paradaUiState.paradaDatos): Boolean {
        return with(uiState){
            longitud.isNotBlank() && latitud.isNotBlank() && nombre.isNotBlank() && direccion.isNotBlank()
        }
    }

    suspend fun guardarParada(){
            try {
                if(validarEntrada()){
                    paradaRepository.insertParada(paradaUiState.paradaDatos.toParada())
                    Log.d("SAVE_PARADA","Guardado exitoso")
                }
            } catch (e: Exception){
                Log.e("SAVE_ERROR", "Error al guardar", e)
            }
        }
}

data class ParadaUiState(
    val paradaDatos: ParadaDatos = ParadaDatos(),
    val entradaValida: Boolean = false
)

data class ParadaDatos(
    val id_parada: Int = 0,
    val longitud: String = "",
    val latitud: String = "",
    val nombre: String = "",
    val direccion: String = "",
    )

fun ParadaDatos.toParada(): Parada = Parada(
    id_parada = id_parada,
    lat = latitud.toDoubleOrNull() ?: 0.0,
    lon = longitud.toDoubleOrNull() ?: 0.0,
    nombre = nombre,
    direccion = direccion,
    estado = true,
)

fun Parada.toParadaUiState(entradaValida: Boolean = false): ParadaUiState = ParadaUiState(
    paradaDatos = this.toParadaDatos(),
    entradaValida = entradaValida
)

fun Parada.toParadaDatos(): ParadaDatos = ParadaDatos(
    id_parada = id_parada,
    latitud = lat.toString(),
    longitud = lon.toString(),
    nombre = nombre?:"",
    direccion = direccion?:"",
)
