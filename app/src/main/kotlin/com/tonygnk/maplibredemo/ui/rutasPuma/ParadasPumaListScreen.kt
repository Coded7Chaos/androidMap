package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination

object ParadasPumaDestination : NavigationDestination {
    override val route = "paradaspuma"
    override val titleRes = R.string.fav_title
    const val rutaIdArg = "rutaId"
    val routeWithArgs = "${ParadasPumaDestination.route}/{$rutaIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParadasPumaListScreen(
    navigateBack: () -> Unit,
    navigateToParada: () -> Unit,
    modifier: Modifier,
    viewModel: ParadasPumaListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val paradaUiState by viewModel.detalleParadas.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MapTopAppBar(
                title = "Paradas",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1) Cabecera dorada con título de ruta
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0A500)),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "ACHUMANI A SAN PEDRO", // aquí pon tu variable de ruta dinámicamente
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            // 2) Banner gris con instrucción
            Text(
                text = "Seleccione la parada donde se encuentra",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9E9E9E))
                    .padding(vertical = 8.dp)
            )

            // 3) Listado en blanco
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(0.dp)    // ② lista sin padding extra arriba
            ) {
                items(paradaUiState, key = { it.id_parada }) { parada ->
                    ParadaItem(parada = parada, onClick = { navigateToParada() })
                }
            }
        }
    }
}
@Composable
fun ParadaItem(
    parada: Parada,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.cara_puma),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        // nombre de parada
        Text(
            text = parada.nombre,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // icono de favorito
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Marcar favorito",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}
