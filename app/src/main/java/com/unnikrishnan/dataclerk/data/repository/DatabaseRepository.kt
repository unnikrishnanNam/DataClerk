package com.unnikrishnan.dataclerk.data.repository

import com.unnikrishnan.dataclerk.data.api.RetrofitClient
import com.unnikrishnan.dataclerk.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for database operations
 */
class DatabaseRepository {
    
    private val apiService = RetrofitClient.apiService
    
    /**
     * Fetch list of available databases
     */
    suspend fun getDatabases(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDatabases()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch databases: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch health status of a specific database
     */
    suspend fun getDatabaseHealth(dbName: String): Result<DatabaseHealth> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDatabaseHealth(dbName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch health: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch schema of a specific database
     * Returns grouped table schemas
     */
    suspend fun getDatabaseSchema(dbName: String): Result<List<TableSchema>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDatabaseSchema(dbName)
            if (response.isSuccessful && response.body() != null) {
                val schemaInfoList = response.body()!!
                
                // Group schema info by table name
                val groupedSchemas = schemaInfoList.groupBy { it.tableName }
                    .map { (tableName, columns) ->
                        TableSchema(
                            tableName = tableName,
                            columns = columns.map { 
                                Triple(it.columnName, it.dataType, "") // No constraints info from API
                            }
                        )
                    }
                    .sortedBy { it.tableName }
                
                Result.success(groupedSchemas)
            } else {
                Result.failure(Exception("Failed to fetch schema: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Execute a SQL query on a database
     */
    suspend fun executeQuery(dbName: String, query: String): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        try {
            val request = ExecuteQueryRequest(database = dbName, query = query)
            val response = apiService.executeQuery(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to execute query: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create DatabaseInfo from health status
     */
    suspend fun getDatabaseInfo(dbName: String, schema: List<TableSchema>): DatabaseInfo {
        val healthResult = getDatabaseHealth(dbName)
        
        return if (healthResult.isSuccess) {
            val health = healthResult.getOrNull()!!
            DatabaseInfo(
                name = dbName,
                status = if (health.status.equals("UP", ignoreCase = true)) 
                    DatabaseStatus.UP else DatabaseStatus.DOWN,
                tableCount = schema.size,
                health = if (health.status.equals("UP", ignoreCase = true)) 
                    "Excellent" else "Down",
                lastUpdated = formatTime(health.currentTime)
            )
        } else {
            DatabaseInfo(
                name = dbName,
                status = DatabaseStatus.UNKNOWN,
                tableCount = schema.size,
                health = "Unknown",
                lastUpdated = "N/A"
            )
        }
    }
    
    /**
     * Format ISO timestamp to readable time
     */
    private fun formatTime(isoTime: String): String {
        return try {
            // Simple formatting - just show "Just now" for demo
            // In production, use proper date formatting
            "Just now"
        } catch (e: Exception) {
            "N/A"
        }
    }
}
