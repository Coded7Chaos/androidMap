package com.tonygnk.maplibredemo.ui.map

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import com.tonygnk.maplibredemo.ui.theme.MapTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tonygnk.maplibredemo.repository.ParadaRutaDetail
import org.maplibre.android.geometry.LatLng

object RouteResultsDestination : NavigationDestination {
    override val route    = "route_results"
    override val titleRes = R.string.route_results_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteResultsScreen(
    viewModel: RouteSearchViewModel,
    onItemClick: () -> Unit
){

    val details by viewModel.detailPairs.collectAsStateWithLifecycle()
    val listaDeRutas by viewModel.rutasEncontradas.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Resultados de la búsqueda", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (details.isEmpty()) {
            Text("No se encontraron rutas", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(details) { detail ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectPair(detail)
                                onItemClick()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            val ruta = listaDeRutas.firstOrNull { it.id_ruta_puma == detail.first.idRuta }
                            Text("Ruta Pumakatari: ${ruta?.nombre}", style = MaterialTheme.typography.bodyLarge)
                            Text("Parada origen: ${detail.first.nombre}",
                                style = MaterialTheme.typography.bodyMedium)
                            Text("Parada destino: ${detail.second.nombre}",
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
