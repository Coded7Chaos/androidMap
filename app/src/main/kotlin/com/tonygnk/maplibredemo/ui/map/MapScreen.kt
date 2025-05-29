package com.tonygnk.maplibredemo.ui.map

import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Network
import android.opengl.Matrix.length
import android.text.TextUtils.replace
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tonygnk.maplibredemo.models.Parada
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
object MapDestination {
    const val route = "main_map"
    val titleRes = R.string.map_title
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
    val lifecycleOwner = LocalLifecycleOwner.current

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
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
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
    //
    // 2. Estado de cámara y marcador
    //
    // Cámara parte centrada en La Paz si no hay permiso ni posición previa
    val cameraPositionState = remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.4897, -68.1193),
                zoom = 12.0
            )
        )
    }
    // Punto origen que esta en la ubicacion del usuario
    val originPoint = remember { mutableStateOf<LatLng?>(null) }

    // Coordenada seleccionada por el usuario (null = sin marcador), representa el destino
    var destinationPoint = remember { mutableStateOf<LatLng?>(null) }
    var isSelectingDestination by remember { mutableStateOf(false) }
    //
    // 3. Al obtener permiso, intento leer la última ubicación conocida
    //
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    // Centro la cámara en la posición del usuario
                    cameraPositionState.value = CameraPosition(userLatLng, 15.0)
                    // Y marco ese punto
                    originPoint.value = userLatLng
                }
            }
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
            /*MapaInteractivo(
                cameraPositionState  = cameraPositionState,
                originPoint = originPoint.value!!,
                destinationPoint = destinationPoint.value!!,
                isSelectingDestination = isSelectingDestination,
                hasLocationPermission = hasLocationPermission,
                onMapClick = { latLng ->
                    if(isSelectingDestination){
                        destinationPoint.value = latLng
                        isSelectingDestination = false
                    }
                }
            )*/

            // ——— Menú inferior “Buscar ruta” + botón retroceso ———

            if (originPoint.value != null && destinationPoint.value != null && !isSelectingDestination) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp) // ajusta para que quede encima del BottomNavBar
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
    originPoint: LatLng,
    destinationPoint: LatLng,
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
            //Marker(

            //)
        }

        // 10. Si hay el punto destino, dibujo un símbolo o círculo en él
        destinationPoint?.let { pos ->
            //Symbol(

            //)
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