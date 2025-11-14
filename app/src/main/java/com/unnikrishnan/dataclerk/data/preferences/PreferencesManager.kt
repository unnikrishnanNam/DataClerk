package com.unnikrishnan.dataclerk.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.unnikrishnan.dataclerk.data.models.AppSettings
import com.unnikrishnan.dataclerk.data.models.ThemeMode

/**
 * Manager for app preferences using SharedPreferences
 */
class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "data_clerk_prefs"
        
        // Keys
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_ENABLE_HAPTICS = "enable_haptics"
        private const val KEY_ENABLE_ANIMATIONS = "enable_animations"
        private const val KEY_AUTO_REFRESH = "auto_refresh"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_SELECTED_DATABASE = "selected_database"
        
        // Defaults
        private const val DEFAULT_BASE_URL = "http://10.227.8.85:8090/api"
        private const val DEFAULT_GEMINI_API_KEY = ""
    }
    
    // Base URL
    var baseUrl: String
        get() = sharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) = sharedPreferences.edit().putString(KEY_BASE_URL, value).apply()
    
    // Gemini API Key
    var geminiApiKey: String
        get() = sharedPreferences.getString(KEY_GEMINI_API_KEY, DEFAULT_GEMINI_API_KEY) 
            ?: DEFAULT_GEMINI_API_KEY
        set(value) = sharedPreferences.edit().putString(KEY_GEMINI_API_KEY, value).apply()
    
    // Enable Haptics
    var enableHaptics: Boolean
        get() = sharedPreferences.getBoolean(KEY_ENABLE_HAPTICS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_ENABLE_HAPTICS, value).apply()
    
    // Enable Animations
    var enableAnimations: Boolean
        get() = sharedPreferences.getBoolean(KEY_ENABLE_ANIMATIONS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_ENABLE_ANIMATIONS, value).apply()
    
    // Auto Refresh
    var autoRefresh: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_REFRESH, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_REFRESH, value).apply()
    
    // Theme Mode
    var themeMode: ThemeMode
        get() {
            val ordinal = sharedPreferences.getInt(KEY_THEME_MODE, ThemeMode.DARK.ordinal)
            return ThemeMode.values().getOrNull(ordinal) ?: ThemeMode.DARK
        }
        set(value) = sharedPreferences.edit().putInt(KEY_THEME_MODE, value.ordinal).apply()
    
    // Selected Database
    var selectedDatabase: String?
        get() = sharedPreferences.getString(KEY_SELECTED_DATABASE, null)
        set(value) = sharedPreferences.edit().putString(KEY_SELECTED_DATABASE, value).apply()
    
    /**
     * Get all settings as AppSettings object
     */
    fun getAppSettings(): AppSettings {
        return AppSettings(
            baseUrl = baseUrl,
            enableHaptics = enableHaptics,
            enableAnimations = enableAnimations,
            autoRefreshInterval = 30, // default value
            theme = themeMode
        )
    }
    
    /**
     * Save all settings from AppSettings object
     */
    fun saveAppSettings(settings: AppSettings) {
        with(sharedPreferences.edit()) {
            putString(KEY_BASE_URL, settings.baseUrl)
            putBoolean(KEY_ENABLE_HAPTICS, settings.enableHaptics)
            putBoolean(KEY_ENABLE_ANIMATIONS, settings.enableAnimations)
            putInt(KEY_THEME_MODE, settings.theme.ordinal)
            apply()
        }
    }
    
    /**
     * Clear all preferences
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Check if Gemini API key is configured
     */
    fun hasGeminiApiKey(): Boolean {
        return geminiApiKey.isNotBlank()
    }
}
