package com.tonygnk.maplibredemo.ui.favoritos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.MapTopAppBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination

object FavoritosDestination : NavigationDestination {
    override val route = "favoritos"
    override val titleRes = R.string.fav_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritosScreen(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
){
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navigateToFavoritos = navigateToFavoritos,
                navigateToProfile= navigateToProfile,
                navigateToRutasPuma= navigateToRutasPuma,
                navigateToMap = navigateToMap,
                selectedItem = "favoritos"
            )
        },
        topBar = {
            MapTopAppBar(
                title = "Rutas guardadas",
                canNavigateBack = false,
                modifier = Modifier,
                scrollBehavior = null,
                navigateUp = navigateToFavoritos
            )
        },
        content = {
            innerPadding ->
            Text(
                text = "No existen rutas guardadas",
                modifier = Modifier.padding(innerPadding),
                fontSize = 30.sp
            )
        }
    )
}