@file:OptIn(ExperimentalMaterial3Api::class)

package com.tonygnk.maplibredemo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tonygnk.maplibredemo.R.string
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.ui.navigation.MapNavHost
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import org.ramani.compose.Polyline

@Composable
fun MapApp(navController: NavHostController = rememberNavController()){
    MapNavHost(navController = navController)
}



@Composable
fun MapTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun BottomNavBar(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
    selectedItem: String
) {
    NavigationBar(
        tonalElevation = 8.dp
    ){
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == "map",
            onClick = {
                navigateToMap()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = selectedItem == "favoritos",
            onClick = {
                navigateToFavoritos()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DirectionsBus, contentDescription = "Rutas") },
            label = { Text("Rutas") },
            selected = selectedItem == "rutas",
            onClick = {
                navigateToRutasPuma()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = selectedItem == "profile",
            onClick = {
                navigateToProfile()
            }
        )
    }
}


@Composable
fun MyMap(
    modifier: Modifier = Modifier,
    puntosList: List<Coordenada>
) {
    val context = LocalContext.current

    // Permisos de ubicación
    var hasLocationPermission by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocationPermission =
            results[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Estado de cámara
    val cameraPositionState: MutableState<CameraPosition> = remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.5, -68.15),
                zoom = 18.0
            )
        )
    }

    // Obtiene última ubicación cuando permisos concedidos
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.value = CameraPosition(userLatLng, zoom = 18.0)
                }
            }
        }
    }

    // Construcción del estilo
    val styleBuilder = remember {
        val styleFile = MapStyleManager(context)
            .setupStyle()
            .let { result ->
                when (result) {
                    is MapStyleManager.StyleSetupResult.Success -> result.styleFile
                    is MapStyleManager.StyleSetupResult.Error   -> throw result.exception
                }
            }
        Style.Builder().fromUri(Uri.fromFile(styleFile).toString())
    }

    // Conversión de coordenadas a LatLng
    fun List<Coordenada>.toLatLngList() = this.map { LatLng(it.lat, it.lon) }
    val puntos = puntosList.toLatLngList()

    // Presentación del mapa
    Box(modifier = modifier.fillMaxSize()) {
        MapLibre(
            modifier = Modifier.fillMaxSize(),
            styleBuilder = styleBuilder,
            cameraPosition = cameraPositionState.value
        ) {
            // Polilínea de ruta
            Polyline(puntos, color = "Red", lineWidth = 5.0f)
            // Indicador de ubicación actual
            val userTarget = cameraPositionState.value.target
            if (hasLocationPermission && userTarget != null) {
                Circle(
                    center = userTarget,
                    radius = 10f,
                    color = "Blue",
                    zIndex = 2
                )
            }
        }
    }
    /*
    val cameraPosition = rememberSaveable {
        CameraPosition(
            target = LatLng(-16.5, -68.15),
            zoom = 18.0,
        )
    }

    MapLibre(
        modifier = modifier,
        styleBuilder = styleBuilder,
        cameraPosition = cameraPosition
    ) {

        fun List<Coordenada>.toLatLngList(): List<LatLng> {
            return this.map { coordenada ->
                LatLng(coordenada.lat, coordenada.lon)
            }
        }
        val puntos = puntosList.toLatLngList()
        Polyline(puntos, color = "Red", lineWidth = 5.0f)

    }

     */
}
