package com.tonygnk.maplibredemo.ui.map

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource




object RouteLoadingDestination : NavigationDestination {
    override val route    = "route_loading"
    override val titleRes = R.string.route_loading_title
}

@Composable
fun RouteLoadingScreen(
    viewModel: RouteSearchViewModel = viewModel (factory = AppViewModelProvider.Factory),
    onResultsReady: (List<Ruta>) -> Unit
){
    LaunchedEffect(Unit) {
        // asume que tienes origin/dest en tu ViewModel o los pasas aquí
        val o = viewModel.getOrigin()
        val d = viewModel.getDestination()
        val results = viewModel.searchRoutes(o, d)
        onResultsReady(results)
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.route_loading_title))
    }
}