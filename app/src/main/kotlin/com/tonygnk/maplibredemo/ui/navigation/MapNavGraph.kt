package com.tonygnk.maplibredemo.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tonygnk.maplibredemo.ui.AppViewModelProvider
import com.tonygnk.maplibredemo.ui.favoritos.FavoritosDestination
import com.tonygnk.maplibredemo.ui.favoritos.FavoritosScreen
import com.tonygnk.maplibredemo.ui.home.HomeDestination
import com.tonygnk.maplibredemo.ui.home.HomeScreen
import com.tonygnk.maplibredemo.ui.map.MapDestination
import com.tonygnk.maplibredemo.ui.map.MapScreen
import com.tonygnk.maplibredemo.ui.map.RouteDetailDestination
//import com.tonygnk.maplibredemo.ui.map.RouteDetailScreen
import com.tonygnk.maplibredemo.ui.map.RouteDetailViewModel
import com.tonygnk.maplibredemo.ui.map.RouteLoadingDestination
import com.tonygnk.maplibredemo.ui.map.RouteLoadingScreen
import com.tonygnk.maplibredemo.ui.map.RouteResultsDestination
import com.tonygnk.maplibredemo.ui.map.RouteResultsScreen
import com.tonygnk.maplibredemo.ui.map.RouteSearchViewModel
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryDestination
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryScreen
import com.tonygnk.maplibredemo.ui.perfil.PerfilDestination
import com.tonygnk.maplibredemo.ui.perfil.PerfilScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasListDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.ParadasListScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaDetailDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaDetailScreen
import com.tonygnk.maplibredemo.ui.rutasPuma.RutaPumaViewModel
import com.tonygnk.maplibredemo.ui.rutasPuma.RutasListDestination
import com.tonygnk.maplibredemo.ui.rutasPuma.RutasListScreen
import androidx.navigation.compose.navigation
import com.tonygnk.maplibredemo.ui.usuario.UserDestination
import com.tonygnk.maplibredemo.ui.usuario.UserScreen


@Composable
fun MapNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){


    NavHost(
        navController = navController,
        startDestination = MapDestination.route, //MapDestination.route,
        modifier = modifier
    ){

        composable(route = MapDestination.route) {
            MapScreen(
                navigateToRutasPuma = { navController.navigate(RutasListDestination.route) },
                navigateToProfile = { navController.navigate(PerfilDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) },
                navigateToLoader = { navController.navigate(RouteLoadingDestination.route) },
                )
        }

        composable(route = FavoritosDestination.route) {
            FavoritosScreen(
                navigateToRutasPuma = { navController.navigate(RutasListDestination.route) },
                navigateToProfile = { navController.navigate(PerfilDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }


        //Lista de rutas del pumakatari
        composable(route = RutasListDestination.route) {
            RutasListScreen(
                navigateToRutasPuma = { navController.navigate(RutasListDestination.route) },
                navigateToProfile = { navController.navigate(PerfilDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) },
                navigateToRuta = { rutaIdArg, rutaNombre ->
                    navController.navigate("${RutaDetailDestination.route}/$rutaIdArg/$rutaNombre")
                }
            )
        }
        navigation(
            startDestination = RutaDetailDestination.routeWithArgs,
            route = "ruta_flow"
        ){
            //Vista de una sola ruta de puma, con la opcion de ver sus paradas
            composable(route = RutaDetailDestination.routeWithArgs,
                arguments = listOf(
                    navArgument(RutaDetailDestination.rutaIdArg) { type = NavType.IntType },
                    navArgument(RutaDetailDestination.rutaNombre) { type = NavType.StringType }
                )
            ) {
                backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("ruta_flow")
                }

                val viewModel: RutaPumaViewModel = viewModel(
                    parentEntry,
                    factory = AppViewModelProvider.Factory
                )

                RutaDetailScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToParadasList = { navController.navigate(ParadasListDestination.route) },
                    viewModel = viewModel
                )
            }

            //Lista de paradas
            composable(route = ParadasListDestination.route) {
                backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("ruta_flow")
                }
                val viewModel: RutaPumaViewModel = viewModel(
                    parentEntry,
                    factory = AppViewModelProvider.Factory
                )

                ParadasListScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToParada = { navController.navigateUp() },
                    modifier = modifier,
                    viewModel = viewModel
                )
            }

        }




        composable(route = PerfilDestination.route) {
            PerfilScreen(
                navigateToRutasPuma = { navController.navigate(RutasListDestination.route) },
                navigateToProfile = { navController.navigate(PerfilDestination.route) },
                navigateToFavoritos = { navController.navigate(FavoritosDestination.route) },
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }





        composable(route = RouteLoadingDestination.route) {
            RouteLoadingScreen(
                onResultsReady  = {
                    navController.navigate(RouteResultsDestination.route) {
                        popUpTo(RouteLoadingDestination.route) { inclusive = true }
                    }
                }
            )
        }
/*
        composable(RouteResultsDestination.route) {
            RouteResultsScreen(

            )
        }
*/

    }

    }
