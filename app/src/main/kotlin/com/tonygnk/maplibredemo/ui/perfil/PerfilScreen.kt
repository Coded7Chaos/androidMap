package com.tonygnk.maplibredemo.ui.perfil

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.R
import com.tonygnk.maplibredemo.ui.map.MapBody
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import java.nio.file.WatchEvent

object ProfileDestination : NavigationDestination {
    override val route = "perfil"
    override val titleRes = R.string.perfil_title
}

@Composable
fun PerfilScreen(
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
                selectedItem = "profile"
            )
        },
        content = {
            innerPadding ->
            Text(text = "Pagina de perfil de usuario", modifier = Modifier.padding(innerPadding))
        }
    )

}