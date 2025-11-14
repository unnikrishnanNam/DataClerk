package com.unnikrishnan.dataclerk.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unnikrishnan.dataclerk.data.models.DatabaseInfo
import com.unnikrishnan.dataclerk.data.models.RecentChat
import com.unnikrishnan.dataclerk.data.models.UiState
import com.unnikrishnan.dataclerk.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Screen
 */
class HomeViewModel : ViewModel() {
    
    private val repository = DatabaseRepository()
    
    private val _databases = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val databases: StateFlow<UiState<List<String>>> = _databases.asStateFlow()
    
    private val _selectedDatabase = MutableStateFlow<String?>(null)
    val selectedDatabase: StateFlow<String?> = _selectedDatabase.asStateFlow()
    
    private val _databaseInfo = MutableStateFlow<UiState<DatabaseInfo>>(UiState.Loading)
    val databaseInfo: StateFlow<UiState<DatabaseInfo>> = _databaseInfo.asStateFlow()
    
    private val _recentChats = MutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats: StateFlow<List<RecentChat>> = _recentChats.asStateFlow()
    
    init {
        loadDatabases()
        loadMockRecentChats() // Mock data for now
    }
    
    private fun loadDatabases() {
        viewModelScope.launch {
            _databases.value = UiState.Loading
            
            val result = repository.getDatabases()
            
            if (result.isSuccess) {
                val dbList = result.getOrNull()!!
                _databases.value = UiState.Success(dbList)
                
                // Auto-select first database
                if (dbList.isNotEmpty() && _selectedDatabase.value == null) {
                    selectDatabase(dbList[0])
                }
            } else {
                _databases.value = UiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Failed to load databases",
                    throwable = result.exceptionOrNull()
                )
            }
        }
    }
    
    fun selectDatabase(dbName: String) {
        _selectedDatabase.value = dbName
        loadDatabaseInfo(dbName)
    }
    
    private fun loadDatabaseInfo(dbName: String) {
        viewModelScope.launch {
            _databaseInfo.value = UiState.Loading
            
            try {
                // Fetch schema to get table count
                val schemaResult = repository.getDatabaseSchema(dbName)
                
                if (schemaResult.isSuccess) {
                    val schema = schemaResult.getOrNull()!!
                    val info = repository.getDatabaseInfo(dbName, schema)
                    _databaseInfo.value = UiState.Success(info)
                } else {
                    _databaseInfo.value = UiState.Error(
                        message = schemaResult.exceptionOrNull()?.message ?: "Failed to load database info"
                    )
                }
            } catch (e: Exception) {
                _databaseInfo.value = UiState.Error(
                    message = e.message ?: "Failed to load database info",
                    throwable = e
                )
            }
        }
    }
    
    fun refresh() {
        loadDatabases()
    }
    
    private fun loadMockRecentChats() {
        // Mock data for now - will be replaced with real chat history later
        _recentChats.value = listOf(
            RecentChat(
                id = "1",
                databaseName = _selectedDatabase.value ?: "testdb",
                title = "Product sales analysis",
                lastMessage = "Show me top 5 products...",
                timestamp = System.currentTimeMillis() - 3600000,
                messageCount = 8
            ),
            RecentChat(
                id = "2",
                databaseName = _selectedDatabase.value ?: "testdb",
                title = "Customer insights",
                lastMessage = "How many active customers?",
                timestamp = System.currentTimeMillis() - 7200000,
                messageCount = 5
            ),
            RecentChat(
                id = "3",
                databaseName = _selectedDatabase.value ?: "postgres",
                title = "Revenue breakdown",
                lastMessage = "Monthly revenue trends",
                timestamp = System.currentTimeMillis() - 86400000,
                messageCount = 12
            )
        )
    }
}
