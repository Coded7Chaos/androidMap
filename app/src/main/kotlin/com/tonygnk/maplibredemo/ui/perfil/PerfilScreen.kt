package com.tonygnk.maplibredemo.ui.perfil

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tonygnk.maplibredemo.BottomNavBar
import com.tonygnk.maplibredemo.R
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tonygnk.maplibredemo.ui.map.MapBody
import com.tonygnk.maplibredemo.ui.navigation.NavigationDestination
import java.nio.file.WatchEvent
import com.tonygnk.maplibredemo.R.drawable.perfil1

object ProfileDestination : NavigationDestination {
    override val route = "perfil"
    override val titleRes = R.string.perfil_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navigateToFavoritos: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToRutasPuma: () -> Unit,
    navigateToMap: () -> Unit,
){
    val darkTheme = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                actions = {
                    Switch(
                        checked = darkTheme.value,
                        onCheckedChange = {
                            darkTheme.value = it
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                navigateToFavoritos = navigateToFavoritos,
                navigateToProfile= navigateToProfile,
                navigateToRutasPuma= navigateToRutasPuma,
                navigateToMap = navigateToMap,
                selectedItem = "profile"
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Imagen de perfil
                Image(
                    painter = painterResource(id = R.drawable.perfil1), // Cambia por tu recurso
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre del usuario
                Text(
                    text = "Franco",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                // Edad del usuario
                Text(
                    text = "28 años",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))
                // Botón de Iniciar Sesión
                Button(
                    onClick = { /* TODO: Acción de login */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de registro
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("No tienes cuenta? ")
                    Text(
                        text = "Regístrate aquí",
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { /* TODO: Navegar a registro */ }
                    )
                }
                //Text(text = "Pagina de perfil de usuario", modifier = Modifier.padding(innerPadding))
            }
        }
    )

}