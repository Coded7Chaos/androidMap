package com.tonygnk.maplibredemo.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.tonygnk.maplibredemo.ui.parada.ParadaEntryDestination


@Composable
fun MapNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route){
            HomeScreen(
                navigateToParadaEntry = { navController.navigate(ParadaEntryDestination.route) },
                navigateToParadaUpdate = {
                    navController.navigate("${ParadaDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = ParadaEntryDestination.route) {
            ParadaEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}