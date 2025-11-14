package com.unnikrishnan.dataclerk.data.models

import java.util.UUID

/**
 * Represents a chat conversation
 */
data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val databaseName: String,
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Represents a single chat message
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis(),
    val contentType: MessageContentType = MessageContentType.TEXT,
    val metadata: MessageMetadata? = null,
    val tableData: TableData? = null,
    val chartData: ChartData? = null
)

/**
 * Message role - user or assistant
 */
enum class MessageRole {
    USER, ASSISTANT
}

/**
 * Type of content in the message
 */
enum class MessageContentType {
    TEXT,           // Plain text or markdown
    TABLE,          // Tabular data
    CHART,          // Chart/Graph data
    CODE,           // Code snippet
    ERROR           // Error message
}

/**
 * Additional metadata for messages
 */
data class MessageMetadata(
    val query: String? = null,           // SQL query if applicable
    val rowCount: Int? = null,           // Number of rows returned
    val executionTime: Long? = null,     // Query execution time in ms
    val error: String? = null            // Error message if any
)

/**
 * Table data for table messages
 */
data class TableData(
    val headers: List<String>,
    val rows: List<List<String>>
)

/**
 * Chart data for chart messages
 */
data class ChartData(
    val type: ChartType,
    val labels: List<String>,
    val values: List<Float>
)

/**
 * Chart types
 */
enum class ChartType {
    BAR, LINE, PIE
}

/**
 * Recent chat preview
 */
data class RecentChat(
    val id: String,
    val databaseName: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val messageCount: Int
)
