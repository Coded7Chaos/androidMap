package com.tonygnk.maplibredemo.ui.home
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import com.tonygnk.maplibredemo.ui.theme.MapTheme
import androidx.compose.runtime.getValue
import com.tonygnk.maplibredemo.models.User

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToParadaEntry: () -> Unit,
    navigateToUserScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MapTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
            FloatingActionButton(
                onClick = navigateToUserScreen,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Text("Pagina de usuarios")
            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToParadaEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.parada_entry_title)
                )
            }
        },

    ) { innerPadding ->
        HomeBody(
            paradaList = homeUiState.paradaList,
            onItemClick = navigateToParadaEntry,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}


@Composable
fun HomeBody(
    paradaList: List<Parada>,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if(paradaList.isEmpty()){
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            MapList(
                paradaList = paradaList,
                onParadaClick = { it.id_parada },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun MapList(
    paradaList: List<Parada>,
    onParadaClick: (Parada) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items (items = paradaList, key = { it.id_parada }) { parada ->
            MapParada(
                parada = parada,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable{ onParadaClick(parada) }
            )
        }
    }

}

@Composable
private fun MapParada(
    parada: Parada, modifier: Modifier = Modifier
){
    Card(
        modifier = modifier,
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
                    text = parada.nombre,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = parada.lon.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = stringResource(R.string.in_stock, parada.id_parada),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
