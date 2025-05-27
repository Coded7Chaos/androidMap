package com.tonygnk.maplibredemo.ui.map

import android.location.Geocoder
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.MapStyleManager
import com.tonygnk.maplibredemo.MyMap
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Coordenada
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import org.ramani.compose.Polyline
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
object MapDestination {
    const val route = "main_map"
    val titleRes = R.string.map_title
}

@Composable
fun MapBody(
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    MyMap(
        modifier = Modifier.padding(contentPadding),
        puntosList = listOf()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val geocoder = remember { Geocoder(context) }

    // Permisos de ubicación precisa
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
    // Solicitar permisos en ON_START
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START && !hasLocationPermission) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Estados para búsqueda y selección
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var selectingPoint by rememberSaveable { mutableStateOf(false) }
    var pickedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Cámara compartida
    var cameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.5, -68.15),
                zoom = 18.0
            )
        )
    }
    // Centrar en ubicación actual cuando permiso otorgado
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                location?.let {
                    val userPos = LatLng(it.latitude, it.longitude)
                    cameraPosition = CameraPosition(userPos, 18.0)
                    pickedLatLng = userPos
                }
            }
        }
    }

    // Estilo offline MBTiles
    val styleBuilder = remember {
        val styleFile = MapStyleManager(context)
            .setupStyle().let { result ->
                when (result) {
                    is MapStyleManager.StyleSetupResult.Success -> result.styleFile
                    is MapStyleManager.StyleSetupResult.Error -> throw result.exception
                }
            }
        Style.Builder().fromUri(Uri.fromFile(styleFile).toString())
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navigateToFavoritos = navigateToFavoritos,
                navigateToProfile = navigateToProfile,
                navigateToRutasPuma = navigateToRutasPuma,
                navigateToMap = navigateToMap,
                selectedItem = "map"
            )
        },
        topBar = {
            // BARRA DE BÚSQUEDA + ICONO MARCADOR
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { text ->
                            searchQuery = text
                            suggestions = if (text.length >= 3) {
                                geocoder.getFromLocationName(text, 5)
                                    ?.mapNotNull { it.getAddressLine(0) }
                                    ?: emptyList()
                            } else emptyList()
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search location") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            geocoder.getFromLocationName(searchQuery, 1)
                                ?.firstOrNull()
                                ?.let {
                                    val target = LatLng(it.latitude, it.longitude)
                                    cameraPosition = CameraPosition(target, 18.0)
                                    pickedLatLng = target
                                }
                            suggestions = emptyList()
                        })
                    )
                    IconButton(onClick = { selectingPoint = !selectingPoint }) {
                        Icon(Icons.Default.Place, contentDescription = "Pick point")
                    }
                }
                // SUGERENCIAS desplegables
                DropdownMenu(
                    expanded = suggestions.isNotEmpty(),
                    onDismissRequest = { suggestions = emptyList() }
                ) {
                    suggestions.forEach { address ->
                        DropdownMenuItem(
                            text = { Text(address) },
                            onClick = {
                                searchQuery = address
                                suggestions = emptyList()
                                geocoder.getFromLocationName(address, 1)
                                    ?.firstOrNull()
                                    ?.let {
                                        val target = LatLng(it.latitude, it.longitude)
                                        cameraPosition = CameraPosition(target, 18.0)
                                        pickedLatLng = target
                                    }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        MapBody(contentPadding = innerPadding)
    }
}