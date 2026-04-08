package com.tripmate.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tripmate.ui.components.BottomNavBar
import com.tripmate.ui.screens.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("ai_generator") {
                com.tripmate.ui.screens.AIGeneratorScreen(navController = navController)
            }
            composable("records") {
                com.tripmate.ui.screens.RecordPlanScreen(navController = navController)
            }
            composable("record_detail/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")
                com.tripmate.ui.screens.TripDetailScreen(navController = navController, tripId = tripId)
            }
            composable("profile") {
                // ProfileScreen(navController = navController)
            }
        }
    }
}
