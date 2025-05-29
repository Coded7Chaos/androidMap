package com.tonygnk.maplibredemo.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tonygnk.maplibredemo.ui.favoritos.FavoritosDestination
import com.tonygnk.maplibredemo.ui.favoritos.FavoritosScreen
import com.tonygnk.maplibredemo.ui.home.HomeDestination
import com.tonygnk.maplibredemo.ui.home.HomeScreen
import com.tonygnk.maplibredemo.ui.map.MapDestination
import com.tonygnk.maplibredemo.ui.map.MapScreen
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryDestination
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryScreen
import com.tonygnk.maplibredemo.ui.perfil.PerfilScreen
import com.tonygnk.maplibredemo.ui.perfil.ProfileDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasPumaDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasPumaListScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaDestination.rutaNombre
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.RutasPumaDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutasPumaListScreen
import com.tonygnk.maplibredemo.ui.usuario.UserDestination
import com.tonygnk.maplibredemo.ui.usuario.UserScreen


@Composable
fun MapNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){


    NavHost(
        navController = navController,
        startDestination = MapDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route){
            HomeScreen(
                navigateToParadaEntry = { navController.navigate(ParadaEntryDestination.route) },
                navigateToUserScreen = { navController.navigate(UserDestination.route) }
            )
        }
        composable(route = ParadaEntryDestination.route) {
            ParadaEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = UserDestination.route) {
            UserScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = MapDestination.route) {
            MapScreen(
                navigateToRutasPuma = { navController.navigate(RutasPumaDestination.route) },
                navigateToProfile = { navController.navigate(ProfileDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }
        composable(route = FavoritosDestination.route) {
            FavoritosScreen(
                navigateToRutasPuma = { navController.navigate(RutasPumaDestination.route) },
                navigateToProfile = { navController.navigate(ProfileDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }

        composable(route = RutasPumaDestination.route) {
            RutasPumaListScreen(
                navigateToRutasPuma = { navController.navigate(RutasPumaDestination.route) },
                navigateToProfile = { navController.navigate(ProfileDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) },
                navigateToRuta = { rutaIdArg, rutaNombre ->
                    navController.navigate("${RutaPumaDestination.route}/$rutaIdArg/$rutaNombre")
                }
            )
        }
        composable(route = ProfileDestination.route) {
            PerfilScreen(
                navigateToRutasPuma = { navController.navigate(RutasPumaDestination.route) },
                navigateToProfile = { navController.navigate(ProfileDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }

        composable(
            route = ParadasPumaDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ParadasPumaDestination.rutaIdArg) { type = NavType.IntType },
            )
        ) {
            ParadasPumaListScreen(
                navigateBack = { navController.navigateUp() },
                navigateToParada = {
                    navController.navigate(ProfileDestination.route)
                },
                modifier = modifier
            )
        }
        composable(route = RutaPumaDestination.routeWithArgs,
                   arguments = listOf(
                       navArgument(RutaPumaDestination.rutaIdArg) { type = NavType.IntType },
                       navArgument(RutaPumaDestination.rutaNombre) { type = NavType.StringType }
                   )
            ) {backStackEntry ->
                val rutaId = backStackEntry.arguments!!.getInt(RutaPumaDestination.rutaIdArg)
                val rutaNombre = backStackEntry.arguments?.getString("rutaNombre")
                RutaPumaScreen(
                    titulo = rutaNombre ?: "Detalle de ruta",
                    navigateBack = { navController.navigateUp() },
                    navigateToParadaList = { navController.navigate("${ParadasPumaDestination.route}/${rutaId}") }
                )
        }

    }
}