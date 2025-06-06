package com.tonygnk.maplibredemo.ui.map

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.MapStyleManager
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.ui.map.RouteDetailDestination.titleRes
import com.tonygnk.maplibredemo.ui.rutasPuma.PumaRutasMap
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaDetailDestination.rutaIdArg
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.ramani.compose.Polyline

object RouteDetailDestination : NavigationDestination {
    override val route    = "route_detail"
    override val titleRes = R.string.route_detail_title
    const val idInicio = "idInicio"
    const val idFinal = "idFinal"
    val routeWithArgs = "${RouteDetailDestination.route}/{$idInicio}/{$idFinal}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack:() -> Unit,
    ){

    val coordenadas by viewModel.puntosRuta.collectAsState()


    val cameraPositionState = remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.4897, -68.1193),
                zoom = 14.0
            )
        )
    }

    LaunchedEffect(coordenadas.puntosList) {
        val lista = coordenadas.puntosList
        if (lista.isNotEmpty()) {
            // Tomamos el primer punto para centrar la cámara
            val first = lista.first()
            cameraPositionState.value = CameraPosition(
                target = LatLng(first.lat, first.lon),
                zoom = 15.0
            )
        }
    }

    Scaffold(
        topBar = {
            MapTopAppBar(
                title = "Ruta encontrada",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        Mapa(
            cameraPositionState = cameraPositionState,
            puntosList = coordenadas.puntosList,
            modifier = Modifier.padding(innerPadding)
        )
    }
}



@Composable
fun Mapa(
    cameraPositionState: MutableState<CameraPosition>,
    puntosList: List<Coordenada> = listOf(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    //Preparo el estilo offline con MapStyleManager
    val styleBuilder = remember {
        val styleManager = MapStyleManager(context)
        val style = when (val result = styleManager.setupStyle()) {
            is MapStyleManager.StyleSetupResult.Error -> {
                throw result.exception
            }

            is MapStyleManager.StyleSetupResult.Success -> result.styleFile
        }
        Style.Builder().fromUri(
            Uri.fromFile(style).toString()
        )
    }

    fun List<Coordenada>.toLatLngList(): List<LatLng> {
        return this.map { coordenada ->
            LatLng(coordenada.lat, coordenada.lon)
        }
    }


    MapLibre(
        modifier = modifier.fillMaxSize(),
        styleBuilder = styleBuilder,
        cameraPosition = cameraPositionState.value
    ) {
        val puntos = puntosList.toLatLngList()
        Polyline(puntos, color = "Red", lineWidth = 5.0f)
    }

    }

