package com.tonygnk.maplibredemo.ui.map

import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.Matrix.length
import android.text.TextUtils.replace
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
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
import com.tonygnk.maplibredemo.MyMap
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
    val geocoder = remember { Geocoder(context) }

    // Connectivity state
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var isOnline by remember { mutableStateOf(connectivityManager.activeNetwork != null) }
    var lastConnectedTime by remember { mutableStateOf("") }
    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                isOnline = true
                lastConnectedTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            override fun onLost(network: android.net.Network) {
                isOnline = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }

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

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var selectingPoint by rememberSaveable { mutableStateOf(false) }
    var pickedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Cámara compartida y geolocalización
    var cameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                target = LatLng(-16.5, -68.15),
                zoom = 18.0
            )
        )
    }
    /*
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
*/

    val resultadosQuery by viewModel.resultadosQuery.collectAsState()

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
            SearchHeader(
                searchValue = viewModel.searchValue,
                updateSearchValue = viewModel::updateSearchValue,
                viewModel = viewModel,
                resultadosQuery = resultadosQuery.nombresParadas
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
        MyMap(
            modifier = Modifier.padding(innerPadding),
            puntosList = listOf(),
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeader(
    searchValue: String,
    updateSearchValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel,
    resultadosQuery: List<String>
){

    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
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
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                updateSearchValue(result)
                                expanded = false
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