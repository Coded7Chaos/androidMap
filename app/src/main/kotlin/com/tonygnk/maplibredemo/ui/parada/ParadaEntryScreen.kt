package com.tonygnk.maplibredemo.ui.parada

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import androidx.compose.material3.Scaffold

object ParadaEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.parada_entry_title
}

@Composable
fun ParadaEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ParadaEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

}