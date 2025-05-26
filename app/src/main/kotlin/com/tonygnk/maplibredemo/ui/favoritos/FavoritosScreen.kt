package com.tonygnk.maplibredemo.ui.favoritos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination

object FavoritosDestination : NavigationDestination {
    override val route = "favoritos"
    override val titleRes = R.string.fav_title
}

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
        content = {
            innerPadding ->
            Text("Pagina de perfil de usuario", modifier = Modifier.padding(innerPadding))
        }
    )
}