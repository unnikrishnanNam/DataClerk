package com.unnikrishnan.dataclerk.data.models

/**
 * User profile information
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val organization: String? = null
)

/**
 * Application settings
 */
data class AppSettings(
    val baseUrl: String = "http://localhost:8090/api",
    val enableHaptics: Boolean = true,
    val enableAnimations: Boolean = true,
    val autoRefreshInterval: Int = 30, // seconds
    val maxRecentChats: Int = 10,
    val theme: ThemeMode = ThemeMode.DARK
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

/**
 * Loading states for UI
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

/**
 * Navigation routes
 */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
    const val SCHEMA_VIEWER = "schema_viewer"
    const val ERROR = "error"
    
    fun chatWithDatabase(databaseName: String) = "chat/$databaseName"
    fun schemaViewer(databaseName: String) = "schema_viewer/$databaseName"
}
