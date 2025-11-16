package com.unnikrishnan.dataclerk.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "chat_conversations")
data class ChatConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val databaseName: String,
    val title: String, // Generated from first message
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val role: String, // USER or ASSISTANT
    val content: String,
    val contentType: String, // TEXT, TABLE, CHART, ERROR
    val timestamp: Long = System.currentTimeMillis(),
    val sqlQuery: String? = null,
    val tableDataJson: String? = null, // JSON serialized TableData
    val chartDataJson: String? = null  // JSON serialized ChartData
)
