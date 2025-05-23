package com.tonygnk.maplibredemo.ui.home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToParadaEntry: () -> Unit,
    navigateToParadaUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
){

}


class HomeScreen {
}