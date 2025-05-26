package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaDestination.rutaIdArg
import androidx.compose.runtime.getValue
import com.tonygnk.maplibredemo.MyMap

object RutaPumaDestination : NavigationDestination {
    override val route = "ruta_puma"
    override val titleRes = R.string.ruta_puma_title
    const val rutaIdArg = "rutaId"
    const val rutaNombre = "rutaNombre"
    val routeWithArgs = "$route/{$rutaIdArg}/{$rutaNombre}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaPumaScreen(
    titulo: String = "Vista de ruta",
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RutaPumaViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coordenadas by viewModel.puntosRuta.collectAsState()


    Scaffold(
        topBar = {
            MapTopAppBar(
                title = titulo,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },  modifier = modifier
    ) { innerPadding ->
        RutaPumaBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                ),
            puntosRuta = coordenadas
        )
    }
}

@Composable
fun RutaPumaBody(
    modifier: Modifier = Modifier,
    puntosRuta: PuntosRuta
) {
    MyMap(modifier,
        puntosList = puntosRuta.puntosList)
}