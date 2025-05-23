package com.tonygnk.maplibredemo.ui.navigation
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface NavigationDestination{
    val route: String
    val titleRes: Int
}