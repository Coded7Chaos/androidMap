package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tonygnk.maplibredemo.PumaRutasMap
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.PumaRutasMap

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
    val paradas by viewModel.detalleParadas.collectAsState()

    Scaffold(
        topBar = {
            MapTopAppBar(
                title = titulo,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF66C2BB),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 25.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // botón de "Paradas"
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Paradas",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Paradas", color = Color.Black)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        RutaPumaBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                ),
            puntosRuta = coordenadas,
            paradas = paradas
        )
    }
}

@Composable
fun RutaPumaBody(
    modifier: Modifier = Modifier,
    puntosRuta: PuntosRuta,
    paradas: List<Parada>
) {
    PumaRutasMap(
        modifier,
        puntosList = puntosRuta.puntosList,
        paradasList = paradas
    )
}