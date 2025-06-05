package com.tonygnk.maplibredemo.ui.rutasPuma

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import com.tonygnk.maplibredemo.ui.theme.Cream

object RutasListDestination : NavigationDestination {
    override val route = "rutas_puma"
    override val titleRes = R.string.rutas_puma_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutasListScreen(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
    navigateToRuta: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RutasPumaListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val rutaUiState by viewModel.rutaUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        // Aplico fondo de pantalla con el color "background" (crema)
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                navigateToFavoritos = navigateToFavoritos,
                navigateToProfile = navigateToProfile,
                navigateToRutasPuma = navigateToRutasPuma,
                navigateToMap = navigateToMap,
                selectedItem = "rutas"
            )
        }
    ) { innerPadding ->
        RutasPumaListBody(
            rutasPumaList = rutaUiState.rutasList,
            onRutaClick = navigateToRuta,
            modifier = modifier
                .fillMaxSize()
                // Aseguro que el contenido no quede detrás del bottom bar
                .padding(innerPadding),
            contentPadding = innerPadding
        )
    }
}

@Composable
fun RutasPumaListBody(
    rutasPumaList: List<Ruta>,
    onRutaClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (rutasPumaList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_routes_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                // Aplico color "onBackground" para que contraste sobre crema
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            RutasList(
                rutasList = rutasPumaList,
                onRutaClick = onRutaClick,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun RutasList(
    rutasList: List<Ruta>,
    onRutaClick: (Int, String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = rutasList,
            key = { it.id_ruta_puma }
        ) { ruta ->
            RutaItem(
                ruta = ruta,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onRutaClick(ruta.id_ruta_puma, ruta.nombre) }
            )
        }
    }
}

@Composable
private fun RutaItem(
    ruta: Ruta,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        // Fondo del Card = "surface" (crema/ligeramente gris según tema)
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFbf915c)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = ruta.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    // Texto siempre "onSurface" para buen contraste sobre el Card
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
