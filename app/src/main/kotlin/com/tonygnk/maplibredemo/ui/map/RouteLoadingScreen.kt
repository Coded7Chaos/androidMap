package com.tonygnk.maplibredemo.ui.map

import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object RouteLoadingDestination : NavigationDestination {
    override val route    = "route_loading"
    override val titleRes = R.string.route_loading_title
}

@Composable
fun RouteLoadingScreen(
    viewModel: RouteSearchViewModel,
    onResultsReady: () -> Unit
) {
    val detailPairs by viewModel.detailPairs.collectAsStateWithLifecycle()
    var loadingMessage by remember { mutableStateOf("Buscando rutas disponibles...") }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(detailPairs) {
        when {
            detailPairs.isNotEmpty() -> {
                onResultsReady()
            }

            detailPairs.isEmpty() && viewModel.routeCandidates.value.isNotEmpty() -> {
                // Caso especial: candidatos pero no pares detallados
                loadingMessage = "Analizando rutas encontradas..."
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            // Esperar un tiempo razonable para la búsqueda
            delay(15000) // 15 segundos

            if (detailPairs.isEmpty()) {
                showError = true
                loadingMessage = "No se encontraron rutas disponibles"
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = loadingMessage,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (showError) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Lógica para reintentar o volver atrás */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Reintentar búsqueda")
                }
            }

        }

    }
}


