package com.tonygnk.maplibredemo.ui
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.savedState
import com.tonygnk.maplibredemo.MapApplication
import com.tonygnk.maplibredemo.ui.favoritos.FavoritosViewModel
import com.tonygnk.maplibredemo.ui.home.HomeViewModel
import com.tonygnk.maplibredemo.ui.map.MapViewModel
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryViewModel
import com.tonygnk.maplibredemo.ui.perfil.PerfilViewModel
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasPumaListScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasPumaListViewModel
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaViewModel
import com.tonygnk.maplibredemo.ui.rutasPuma.RutasPumaListViewModel
import com.tonygnk.maplibredemo.ui.usuario.UserViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ParadaEntryViewModel(mapApplication().container.paradaRepository)
        }
        initializer {
            HomeViewModel(mapApplication().container.paradaRepository)
        }
        initializer {
            UserViewModel(mapApplication().container.userRepository)
        }
        initializer {
            MapViewModel(mapApplication().container.paradaRepository)
        }
        initializer {
            PerfilViewModel()
        }
        initializer {
            FavoritosViewModel()
        }
        initializer {
            RutasPumaListViewModel(mapApplication().container.rutaRepository)
        }
        initializer {
            ParadasPumaListViewModel(
                mapApplication().container.paradaRepository,
                mapApplication().container.paradaRutaRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
        initializer {
            RutaPumaViewModel(
                mapApplication().container.rutaRepository,
                mapApplication().container.paradaRutaRepository,
                mapApplication().container.paradaRepository,
                savedStateHandle = createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.mapApplication(): MapApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MapApplication)