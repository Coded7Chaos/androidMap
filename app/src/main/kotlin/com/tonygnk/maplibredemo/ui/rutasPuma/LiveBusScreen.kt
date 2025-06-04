package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination

object LiveBusDestination : NavigationDestination {
    override val route = "parada_puma"
    override val titleRes = R.string.ruta_puma_title
    const val rutaIdArg = "rutaId"
    const val paradaIdArg = "paradaId"
    val routeWithArgs = "$route/{$rutaIdArg}/{$paradaIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBusScreen(
    titulo: String = "Parada",
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LiveBusViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coordenadas by viewModel.puntosRuta.collectAsState()
    val paradas by viewModel.detalleParadas.collectAsState()

    val parada by viewModel.parada.collectAsState()

    Scaffold(
        topBar = {
            MapTopAppBar(
                title = titulo,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        RutaPumaParadaBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                ),
            puntosRuta = coordenadas,
            paradas = paradas,
        )
    }
}

@Composable
fun RutaPumaParadaBody(
    modifier: Modifier = Modifier,
    puntosRuta: PuntosRuta,
    paradas: List<Parada>,
) {

}