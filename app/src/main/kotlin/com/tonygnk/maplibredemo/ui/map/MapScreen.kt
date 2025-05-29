package com.tonygnk.maplibredemo.ui.map


import android.net.ConnectivityManager
import android.net.Uri
import android.Manifest
import android.R.attr.priority
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager

import android.net.Network
import android.opengl.Matrix.length
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils.replace
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.MapStyleManager
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import org.ramani.compose.Polyline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import java.time.LocalDateTime
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.suspendCancellableCoroutine
import org.ramani.compose.Symbol
import java.time.format.DateTimeFormatter
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@OptIn(ExperimentalMaterial3Api::class)
object MapDestination {
    const val route = "main_map"
    val titleRes = R.string.map_title
}

// --------------------------------------------
// 1) Helper para await en coroutines
// --------------------------------------------

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitLocationOrNull(context: Context): android.location.Location? {
    // 1) Verifico permiso justo antes
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) return null

    // 2) Creo la fuente de cancelación
    val tokenSource = CancellationTokenSource()

    return suspendCancellableCoroutine { cont ->
        // 3) Llamo al API usando el token de la fuente
        this.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            tokenSource.token
        ).addOnSuccessListener { loc ->
            cont.resume(loc)
        }.addOnFailureListener {
            cont.resume(null)
        }

        // 4) Si la coroutine se cancela, cancelo el tokenSource
        cont.invokeOnCancellation {
            tokenSource.cancel()  // ← aquí sí existe: CancellationTokenSource.cancel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    //
    // 1. Permisos de ubicación (se piden en ON_START si no están)
    //
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        hasLocationPermission = granted
        Log.d("MapScreenDebug", "Permiso FINE_LOCATION granted=$granted")
    }



    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            Log.d("MapScreenDebug", "Lanzando diálogo de permiso")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("MapScreenDebug", "Ya tengo permiso")
        }
    }
    //
    // 2. Estado de cámara y marcador
    //
    // Cámara parte centrada en La Paz si no hay permiso ni posición previa
    val cameraPositionState = remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.4897, -68.1193),
                zoom = 14.0
            )
        )
    }
    // Punto origen que esta en la ubicacion del usuario
    val originPoint = remember { mutableStateOf<LatLng?>(null) }

    val fusedClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Coordenada seleccionada por el usuario (null = sin marcador), representa el destino
    var destinationPoint = remember { mutableStateOf<LatLng?>(null) }
    var isSelectingDestination by remember { mutableStateOf(false) }



    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            // Intento rápido con lastLocation
            fusedClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        Log.d("MapScreenDebug", "← lastLocation OK: $loc")
                        val p = LatLng(loc.latitude, loc.longitude)
                        cameraPositionState.value = CameraPosition(p, 15.0)
                        originPoint.value = p
                    } else {
                        Log.d("MapScreenDebug", "← lastLocation null, suscribiendo fused updates")
                        // Configuro Fused updates
                        val request = LocationRequest.create().apply {
                            priority = Priority.PRIORITY_HIGH_ACCURACY
                            interval = 5_000L
                            fastestInterval = 2_000L
                        }
                        val fusedCallback = object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                result.lastLocation?.let { location ->
                                    Log.d("MapScreenDebug", "← fused onLocationResult: $location")
                                    val p = LatLng(location.latitude, location.longitude)
                                    cameraPositionState.value = CameraPosition(p, 15.0)
                                    originPoint.value = p
                                    fusedClient.removeLocationUpdates(this)
                                }
                            }
                        }
                        fusedClient.requestLocationUpdates(
                            request,
                            fusedCallback,
                            Looper.getMainLooper()
                        )
                        onDispose {
                            Log.d("MapScreenDebug", "→ Removing fused updates")
                            fusedClient.removeLocationUpdates(fusedCallback)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("MapScreenDebug", "FusedLocation failed, fallback to LocationManager: $e")
                    // Fallback a LocationManager
                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val gpsListener = object : LocationListener {
                        override fun onLocationChanged(loc: android.location.Location) {
                            Log.d("MapScreenDebug", "← GPS onLocationChanged: $loc")
                            val p = LatLng(loc.latitude, loc.longitude)
                            cameraPositionState.value = CameraPosition(p, 15.0)
                            originPoint.value = p
                            lm.removeUpdates(this)
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }
                    // Pido una sola actualización GPS
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        lm.requestSingleUpdate(
                            LocationManager.GPS_PROVIDER,
                            gpsListener,
                            Looper.getMainLooper()
                        )
                    }
                }

            onDispose { /* nada más que limpiar aquí */ }
        }
    }

    // Connectivity state
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isOnline by remember { mutableStateOf(connectivityManager.activeNetwork != null) }
    var lastConnectedTime by remember { mutableStateOf("") }
    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isOnline = true
                lastConnectedTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            override fun onLost(network: Network) {
                isOnline = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }



    val resultadosQuery by viewModel.resultadosQuery.collectAsState()
    // Estado para latitud y longitud; null = sin punto
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }


    // Función que actualiza el estado: al cambiar lat y lng
    fun updateCoordinates(newLat: Double, newLng: Double) {
        latitude = newLat
        longitude = newLng
    }


    Scaffold(
        topBar = {
            SearchHeader(
                searchValue = viewModel.searchValue,
                updateSearchValue = viewModel::updateSearchValue,
                onCenterClick = {
                    val centro = cameraPositionState.value.target
                    centro?.let {
                        isSelectingDestination = true
                        cameraPositionState.value.target?.let{
                            destinationPoint.value = it
                        }
                    }
                },
                onResultClick = { lat, lng ->
                    isSelectingDestination = true
                    val destino = LatLng(lat, lng)
                    destinationPoint.value = destino
                    cameraPositionState.value =
                    CameraPosition(destino, 14.0)
                },
                viewModel = viewModel,
                resultadosQuery = resultadosQuery.nombresParadas
            )
        },

        bottomBar = {
            BottomNavBar(
                navigateToFavoritos = navigateToFavoritos,
                navigateToProfile = navigateToProfile,
                navigateToRutasPuma = navigateToRutasPuma,
                navigateToMap = navigateToMap,
                selectedItem = "map"
            )
        },

        floatingActionButton = {
            ConnectivityStatus(
                isOnline = isOnline,
                lastConnectedTime = lastConnectedTime,
                modifier = Modifier
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 5. Componente de mapa puro
            MapaInteractivo(
                cameraPositionState  = cameraPositionState,
                originPoint = originPoint.value,
                destinationPoint = destinationPoint.value,
                isSelectingDestination = isSelectingDestination,
                hasLocationPermission = hasLocationPermission,
                onMapClick = { latLng ->
                    if(isSelectingDestination){
                        destinationPoint.value = latLng
                        isSelectingDestination = false
                    }
                }
            )
            // Tamaño de la cruz
            val crossSize: Dp = 16.dp
            // Grosor de la línea
            val strokeWidthDp: Dp = 1.dp
            Canvas(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(crossSize)
            ) {
                val strokeWidthPx = strokeWidthDp.toPx()
                // centro del Canvas
                val cx = size.width / 2
                val cy = size.height / 2

                // Línea vertical
                drawLine(
                    color = Color.Black.copy(alpha = 0.6f),
                    start = Offset(cx, 0f),
                    end = Offset(cx, size.height),
                    strokeWidth = strokeWidthPx
                )
                // Línea horizontal
                drawLine(
                    color = Color.Black.copy(alpha = 0.6f),
                    start = Offset(0f, cy),
                    end = Offset(size.width, cy),
                    strokeWidth = strokeWidthPx
                )
            }

            // ——— Menú inferior “Buscar ruta” + botón retroceso ———

            if (originPoint.value != null && destinationPoint.value != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(0.dp) // ajusta para que quede encima del BottomNavBar
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            destinationPoint.value = null
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                            .size(32.dp) // pequeño círculo
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retroceder",
                            tint = Color.White
                        )
                    }
                    Button(
                        onClick = { /* TODO: lanzar búsqueda de ruta */ },
                        modifier = Modifier.align(Alignment.Center)
                            .height(48.dp)
                    ) {
                    Text("Buscar ruta")
                    }
                }
            }

        }
    }
}



@Composable
fun MapaInteractivo(
    cameraPositionState: MutableState<CameraPosition>,
    originPoint: LatLng?,
    destinationPoint: LatLng?,
    isSelectingDestination: Boolean,
    hasLocationPermission: Boolean,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current


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

    MapLibre(
        modifier = modifier.fillMaxSize(),
        styleBuilder = styleBuilder,
        cameraPosition = cameraPositionState.value
    ) {

        // 10. Si hay el punto de origen, dibujo un símbolo o círculo en él
        originPoint?.let { pos ->
            /*Symbol(
                center = LatLng(pos.latitude, pos.longitude),
                imageId = R.drawable.persona,
                color = "black",
                isDraggable = false,
                size = 0.03f
            )*/
            Circle(
                center = pos,
                radius = 8f,
                color  = "Blue",
                zIndex = 2
            )

        }

        // 10. Si hay el punto destino, dibujo un símbolo o círculo en él
        destinationPoint?.let { pos ->
            /*Symbol(
                center = LatLng(pos.latitude, pos.longitude),
                imageId = R.drawable.parada_bus,
                color = "black",
                isDraggable = true,
                size = 0.03f
            )*/
            Circle(
                center = pos,
                radius = 8f,
                color  = "Red",
                zIndex = 3
            )
        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeader(
    searchValue: String,
    updateSearchValue: (String) -> Unit,
    onCenterClick: () -> Unit,
    onResultClick: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel,
    resultadosQuery: List<Parada>
){

    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .matchParentSize()
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchValue,
                    onQueryChange = { updateSearchValue(it)},
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    trailingIcon = {
                        IconButton(onClick = {
                            onCenterClick()
                            expanded = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Marcar centro"
                            )
                        }
                    },
                    placeholder = { Text("Busca una parada por su nombre") },
                    onSearch = {}
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {

            // Display search results in a scrollable column
            Column(Modifier.verticalScroll(rememberScrollState())) {
                resultadosQuery.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result.nombre) },
                        modifier = Modifier
                            .clickable {
                                updateSearchValue(result.nombre)
                                expanded = false
                                onResultClick(result.lat, result.lon)
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectivityStatus(
    isOnline: Boolean,
    lastConnectedTime: String,
    modifier: Modifier = Modifier
){
    // Connectivity indicator
    Column(modifier = Modifier
        .background(
            color = if(isOnline) Color(0xFF4CAF50) else Color(0xFF072A8C),
            shape = RoundedCornerShape(3.dp)
        )
        .padding(horizontal = 8.dp, vertical = 4.dp)) {

        Text(
            text = if (isOnline) "online" else "offline",
            color = Color.White,
            fontSize = 15.sp
        )
        if (!isOnline) {
            Text(
                text = "Ultima conexión: $lastConnectedTime",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}