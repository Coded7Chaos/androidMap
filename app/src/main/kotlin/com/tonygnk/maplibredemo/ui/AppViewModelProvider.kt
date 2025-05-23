package com.tonygnk.maplibredemo.ui
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tonygnk.maplibredemo.MapApplication
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ParadaEntryViewModel(mapApplication().container.paradaRepository)
        }
    }
}

fun CreationExtras.mapApplication(): MapApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MapApplication)