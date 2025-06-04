package com.tonygnk.maplibredemo.ui.rutasPuma

import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tonygnk.maplibredemo.MapStyleManager
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Parada
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre
import org.ramani.compose.Polyline
import org.ramani.compose.Symbol

object RutaDetailDestination : NavigationDestination {
    override val route = "ruta_flow/ruta_puma"
    override val titleRes = R.string.ruta_puma_title
    const val rutaIdArg = "rutaId"
    const val rutaNombre = "rutaNombre"
    val routeWithArgs = "$route/{$rutaIdArg}/{$rutaNombre}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailScreen(
    titulo: String = "Vista de ruta",
    navigateBack: () -> Unit,
    navigateToParadasList: () -> Unit,
    viewModel: RutaPumaViewModel,
    modifier: Modifier = Modifier
){
    val coordenadas by viewModel.puntosRuta.collectAsState()
    val paradas by viewModel.detalleParadas.collectAsState()
    val rutaId = viewModel.rutaId
    val cameraPosition by viewModel.cameraPositionState
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
                        onClick ={ navigateToParadasList() },
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
        RutaDetailBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                ),
            puntosRuta = coordenadas,
            paradas = paradas,
            cameraPosition = cameraPosition
        )
    }
}

@Composable
fun RutaDetailBody(
    modifier: Modifier = Modifier,
    puntosRuta: PuntosRuta,
    paradas: List<Parada>,
    cameraPosition: CameraPosition
) {
    PumaRutasMap(
        puntosList = puntosRuta.puntosList,
        paradasList = paradas,
        cameraPosition = cameraPosition
    )
}


@Composable
fun PumaRutasMap(
    cameraPosition: CameraPosition,
    modifier: Modifier = Modifier,
    puntosList: List<Coordenada> = listOf(),
    paradasList: List<Parada> = listOf()
) {
    val context = LocalContext.current

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

    MapLibre(
        modifier = modifier,
        styleBuilder = styleBuilder,
        cameraPosition = cameraPosition,
    ) {
        // Add map markers, polylines, etc.
        var point = LatLng(-16.5, -68.15)
        Symbol(point, 0.5f, "red", false)
        fun List<Coordenada>.toLatLngList(): List<LatLng> {
            return this.map { coordenada ->
                LatLng(coordenada.lat, coordenada.lon)
            }
        }

        fun List<Parada>.toLatLngList(): List<LatLng> {
            return this.map { parada ->
                LatLng(parada.lat, parada.lon)
            }
        }


        val puntos = puntosList.toLatLngList()
        Polyline(puntos, color = "Red", lineWidth = 5.0f)

        paradasList.forEach { parada ->
            Symbol(
                center = LatLng(parada.lat, parada.lon),
                imageId = R.drawable.parada_bus,
                color = "black",
                isDraggable = false,
                size = 0.03f
            )
        }

    }
}

