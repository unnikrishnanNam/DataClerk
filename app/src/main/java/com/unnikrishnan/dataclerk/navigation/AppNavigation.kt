package com.unnikrishnan.dataclerk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unnikrishnan.dataclerk.data.models.Routes
import com.unnikrishnan.dataclerk.data.preferences.PreferencesManager
import com.unnikrishnan.dataclerk.ui.screens.chat.ChatScreen
import com.unnikrishnan.dataclerk.ui.screens.error.ErrorScreen
import com.unnikrishnan.dataclerk.ui.screens.home.HomeScreen
import com.unnikrishnan.dataclerk.ui.screens.schema.SchemaViewerScreen
import com.unnikrishnan.dataclerk.ui.screens.settings.SettingsScreen
import com.unnikrishnan.dataclerk.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        // Splash Screen
        composable(route = Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToError = {
                    navController.navigate(Routes.ERROR) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen
        composable(route = Routes.HOME) {
            HomeScreen(
                onNavigateToChat = { databaseName ->
                    navController.navigate("${Routes.CHAT}/$databaseName")
                },
                onNavigateToSchema = { databaseName ->
                    navController.navigate("${Routes.SCHEMA_VIEWER}/$databaseName")
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        
        // Chat Screen with database parameter
        composable(
            route = "${Routes.CHAT}/{databaseName}",
            arguments = listOf(
                navArgument("databaseName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val databaseName = backStackEntry.arguments?.getString("databaseName") ?: ""
            val geminiApiKey = prefsManager.geminiApiKey.takeIf { it.isNotBlank() }
            ChatScreen(
                databaseName = databaseName,
                geminiApiKey = geminiApiKey,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Schema Viewer Screen
        composable(
            route = "${Routes.SCHEMA_VIEWER}/{databaseName}",
            arguments = listOf(
                navArgument("databaseName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val databaseName = backStackEntry.arguments?.getString("databaseName") ?: ""
            SchemaViewerScreen(
                databaseName = databaseName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Settings Screen
        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Error Screen
        composable(route = Routes.ERROR) {
            ErrorScreen(
                onRetry = {
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
