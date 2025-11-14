package com.unnikrishnan.dataclerk.ui.screens.schema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unnikrishnan.dataclerk.data.models.TableSchema
import com.unnikrishnan.dataclerk.data.models.UiState
import com.unnikrishnan.dataclerk.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Schema Viewer Screen
 */
class SchemaViewModel : ViewModel() {
    
    private val repository = DatabaseRepository()
    
    private val _tables = MutableStateFlow<UiState<List<TableSchema>>>(UiState.Loading)
    val tables: StateFlow<UiState<List<TableSchema>>> = _tables.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filteredTables = MutableStateFlow<List<TableSchema>>(emptyList())
    val filteredTables: StateFlow<List<TableSchema>> = _filteredTables.asStateFlow()
    
    private val _expandedTables = MutableStateFlow<Set<String>>(emptySet())
    val expandedTables: StateFlow<Set<String>> = _expandedTables.asStateFlow()
    
    fun loadSchema(databaseName: String) {
        viewModelScope.launch {
            _tables.value = UiState.Loading
            
            val result = repository.getDatabaseSchema(databaseName)
            
            if (result.isSuccess) {
                val tableList = result.getOrNull()!!
                _tables.value = UiState.Success(tableList)
                _filteredTables.value = tableList
            } else {
                _tables.value = UiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Failed to load schema",
                    throwable = result.exceptionOrNull()
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterTables(query)
    }
    
    private fun filterTables(query: String) {
        val currentTables = (_tables.value as? UiState.Success)?.data ?: return
        
        if (query.isBlank()) {
            _filteredTables.value = currentTables
        } else {
            _filteredTables.value = currentTables.filter { table ->
                table.tableName.contains(query, ignoreCase = true) ||
                table.columns.any { (columnName, _, _) ->
                    columnName.contains(query, ignoreCase = true)
                }
            }
        }
    }
    
    fun toggleTableExpansion(tableName: String) {
        val current = _expandedTables.value
        _expandedTables.value = if (current.contains(tableName)) {
            current - tableName
        } else {
            current + tableName
        }
    }
    
    fun refresh(databaseName: String) {
        loadSchema(databaseName)
    }
}
