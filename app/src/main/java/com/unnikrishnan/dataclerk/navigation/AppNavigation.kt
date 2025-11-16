package com.unnikrishnan.dataclerk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unnikrishnan.dataclerk.data.models.Routes
import com.unnikrishnan.dataclerk.data.preferences.PreferencesManager
import com.unnikrishnan.dataclerk.ui.screens.chat.ChatScreen
import com.unnikrishnan.dataclerk.ui.screens.error.ErrorScreen
import com.unnikrishnan.dataclerk.ui.screens.history.HistoryScreen
import com.unnikrishnan.dataclerk.ui.screens.history.HistoryViewModel
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
                onNavigateToChatWithId = { databaseName, conversationId ->
                    navController.navigate("${Routes.CHAT}/$databaseName?conversationId=$conversationId")
                },
                onNavigateToSchema = { databaseName ->
                    navController.navigate("${Routes.SCHEMA_VIEWER}/$databaseName")
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToHistory = { databaseName ->
                    navController.navigate("${Routes.HISTORY}/$databaseName")
                }
            )
        }
        
        // Chat Screen with database parameter and optional conversationId
        composable(
            route = "${Routes.CHAT}/{databaseName}?conversationId={conversationId}",
            arguments = listOf(
                navArgument("databaseName") {
                    type = NavType.StringType
                },
                navArgument("conversationId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val databaseName = backStackEntry.arguments?.getString("databaseName") ?: ""
            val conversationId = backStackEntry.arguments?.getLong("conversationId")?.takeIf { it != -1L }
            val geminiApiKey = prefsManager.geminiApiKey.takeIf { it.isNotBlank() }
            ChatScreen(
                databaseName = databaseName,
                geminiApiKey = geminiApiKey,
                conversationId = conversationId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = { dbName ->
                    navController.navigate("${Routes.HISTORY}/$dbName")
                }
            )
        }
        
        // History Screen
        composable(
            route = "${Routes.HISTORY}/{databaseName}",
            arguments = listOf(
                navArgument("databaseName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val databaseName = backStackEntry.arguments?.getString("databaseName") ?: ""
            val viewModel: HistoryViewModel = viewModel()
            val conversations by viewModel.conversations.collectAsState()
            
            // Load conversations when screen appears
            viewModel.loadConversations(databaseName)
            
            HistoryScreen(
                databaseName = databaseName,
                conversations = conversations,
                onBackClick = {
                    navController.popBackStack()
                },
                onConversationClick = { conversationId ->
                    navController.navigate("${Routes.CHAT}/$databaseName?conversationId=$conversationId")
                },
                onDeleteConversation = { conversationId ->
                    viewModel.deleteConversation(conversationId)
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
