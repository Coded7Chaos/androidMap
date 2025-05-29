package com.tonygnk.maplibredemo.ui.map

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination

object RouteDetailDestination : NavigationDestination {
    override val route    = "route_detail/{routeId}"
    override val titleRes = R.string.route_detail_title

    const val routeIdArg = "routeId"
    val routeWithoutArgs = "route_detail"
    val routeWithArgs = "$routeWithoutArgs/{$routeIdArg}"
}
/*
@Composable
fun RouteDetailScreen(
    viewModel: RouteDetailViewModel,
    routeId: Int,
    navigateUp:() -> Unit
){
    val route by produceState<Ruta?>(null) {
        value = viewModel.loadDetail()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Ruta #$routeId") },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        )
    }) {
        route?.let {
            Text("Detalle:\n${it.name}\nStops: ${it.stops.size}", Modifier.padding(16.dp))
        }
    }
}*/