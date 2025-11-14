package com.unnikrishnan.dataclerk.data.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a database health check response
 */
data class DatabaseHealth(
    @SerializedName("database")
    val database: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("current_time")
    val currentTime: String
)

/**
 * Represents a database schema item
 */
data class SchemaInfo(
    @SerializedName("table_name")
    val tableName: String,
    @SerializedName("column_name")
    val columnName: String,
    @SerializedName("data_type")
    val dataType: String
)

/**
 * Represents a grouped table schema
 */
data class TableSchema(
    val tableName: String,
    val columns: List<Triple<String, String, String>> // columnName, dataType, constraints
)

/**
 * Represents column information
 */
typealias ColumnInfo = Triple<String, String, String>

/**
 * Request body for executing queries
 */
data class ExecuteQueryRequest(
    @SerializedName("Database")
    val database: String,
    @SerializedName("Query")
    val query: String
)

/**
 * Response for query execution
 */
data class QueryResult(
    val data: List<Map<String, Any>>
)

/**
 * Database info for display
 */
data class DatabaseInfo(
    val name: String,
    val status: DatabaseStatus,
    val tableCount: Int = 0,
    val health: String = "Unknown",
    val lastUpdated: String = ""
)

enum class DatabaseStatus {
    UP, DOWN, UNKNOWN
}
