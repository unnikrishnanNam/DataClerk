package com.unnikrishnan.dataclerk.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unnikrishnan.dataclerk.data.repository.DatabaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Splash Screen
 */
class SplashViewModel : ViewModel() {
    
    private val repository = DatabaseRepository()
    
    private val _loadingText = MutableStateFlow("Connecting to backend...")
    val loadingText: StateFlow<String> = _loadingText.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<NavigationEvent>(NavigationEvent.None)
    val navigationEvent: StateFlow<NavigationEvent> = _navigationEvent.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                delay(800)
                _loadingText.value = "Loading databases..."
                
                // Attempt to fetch databases
                val result = repository.getDatabases()
                
                if (result.isSuccess) {
                    delay(500)
                    _loadingText.value = "Fetching schemas..."
                    delay(500)
                    _loadingText.value = "Ready!"
                    delay(300)
                    _navigationEvent.value = NavigationEvent.NavigateToHome
                } else {
                    _navigationEvent.value = NavigationEvent.NavigateToError
                }
            } catch (e: Exception) {
                _navigationEvent.value = NavigationEvent.NavigateToError
            }
        }
    }
    
    fun retry() {
        _navigationEvent.value = NavigationEvent.None
        _loadingText.value = "Connecting to backend..."
        loadInitialData()
    }
    
    fun clearNavigationEvent() {
        _navigationEvent.value = NavigationEvent.None
    }
    
    sealed class NavigationEvent {
        object None : NavigationEvent()
        object NavigateToHome : NavigationEvent()
        object NavigateToError : NavigationEvent()
    }
}
