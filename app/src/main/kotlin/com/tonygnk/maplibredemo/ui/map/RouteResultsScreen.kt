package com.tonygnk.maplibredemo.ui.map

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
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
import com.tonygnk.maplibredemo.repository.ParadaRutaDetail

object RouteResultsDestination : NavigationDestination {
    override val route    = "route_results"
    override val titleRes = R.string.route_results_title
}

@Composable
fun RouteResultsScreen(
    viewModel: RouteSearchViewModel,
    onItemClick: (Ruta) -> Unit
){
    val candidates by viewModel.detailPairs.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.route_results_title)) })
    }) { padding ->
        RouteResultsBody(
            candidates         = candidates,
            onCandidateClick   = { (a, b) -> onCandidateSelected(a, b) },
            contentPadding     = padding
        )
    }
}

@Composable
fun RouteResultsBody(
    candidates: List<Pair<ParadaRutaDetail, ParadaRutaDetail>>,
    onCandidateClick: (Pair<ParadaRutaDetail, ParadaRutaDetail>) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (candidates.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            CandidateList(
                candidates     = candidates,
                onCandidateClick = onCandidateClick,
                contentPadding = contentPadding,
                modifier       = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun CandidateList(
    candidates: List<Pair<ParadaRutaDetail, ParadaRutaDetail>>,
    onCandidateClick: (Pair<ParadaRutaDetail, ParadaRutaDetail>) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        itemsIndexed(candidates, key = { index, _ -> index }) { index, candidate ->
            CandidateItem(
                index     = index,
                candidate = candidate,
                modifier  = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onCandidateClick(candidate) }
            )
        }
    }
}

@Composable
private fun CandidateItem(
    index: Int,
    candidate: Pair<ParadaRutaDetail, ParadaRutaDetail>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text  = "Opción ${index + 1}",
                style = MaterialTheme.typography.titleLarge,
            )
            // Aquí más detalles si quieres, por ahora lo dejamos simple
        }
    }
}