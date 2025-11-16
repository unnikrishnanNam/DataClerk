package com.unnikrishnan.dataclerk.data.repository

import android.content.Context
import com.google.gson.Gson
import com.unnikrishnan.dataclerk.data.local.ChatDao
import com.unnikrishnan.dataclerk.data.local.ChatConversationEntity
import com.unnikrishnan.dataclerk.data.local.ChatDatabase
import com.unnikrishnan.dataclerk.data.local.ChatMessageEntity
import com.unnikrishnan.dataclerk.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing chat conversation history
 */
class ChatHistoryRepository(context: Context) {
    
    private val chatDao: ChatDao = ChatDatabase.getDatabase(context).chatDao()
    private val gson = Gson()
    
    /**
     * Create a new conversation
     */
    suspend fun createConversation(databaseName: String, firstMessage: String): Long {
        val title = generateTitle(firstMessage)
        val conversation = ChatConversationEntity(
            databaseName = databaseName,
            title = title,
            messageCount = 0
        )
        return chatDao.insertConversation(conversation)
    }
    
    /**
     * Save a message to a conversation
     */
    suspend fun saveMessage(
        conversationId: Long,
        message: ChatMessage
    ) {
        val messageEntity = ChatMessageEntity(
            conversationId = conversationId,
            role = message.role.name,
            content = message.content,
            contentType = message.contentType.name,
            sqlQuery = message.metadata?.query,
            tableDataJson = message.tableData?.let { gson.toJson(it) },
            chartDataJson = message.chartData?.let { gson.toJson(it) }
        )
        
        chatDao.insertMessage(messageEntity)
        
        // Update conversation
        val conversation = chatDao.getConversationById(conversationId)
        conversation?.let {
            val updatedConversation = it.copy(
                updatedAt = System.currentTimeMillis(),
                messageCount = it.messageCount + 1
            )
            chatDao.updateConversation(updatedConversation)
        }
    }
    
    /**
     * Get all messages for a conversation
     */
    fun getConversationMessages(conversationId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByConversation(conversationId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id.toString(),
                    role = MessageRole.valueOf(entity.role),
                    content = entity.content,
                    contentType = MessageContentType.valueOf(entity.contentType),
                    timestamp = entity.timestamp,
                    metadata = entity.sqlQuery?.let { 
                        MessageMetadata(
                            query = it,
                            executionTime = 0L,
                            rowCount = 0
                        ) 
                    },
                    tableData = entity.tableDataJson?.let { 
                        gson.fromJson(it, TableData::class.java) 
                    },
                    chartData = entity.chartDataJson?.let { 
                        gson.fromJson(it, ChartData::class.java) 
                    }
                )
            }
        }
    }
    
    /**
     * Get recent conversations
     */
    fun getRecentConversations(limit: Int = 10): Flow<List<ChatConversationEntity>> {
        return chatDao.getRecentConversations(limit)
    }
    
    /**
     * Get conversations by database
     */
    fun getConversationsByDatabase(databaseName: String): Flow<List<ChatConversationEntity>> {
        return chatDao.getConversationsByDatabase(databaseName)
    }
    
    /**
     * Get conversation by ID
     */
    suspend fun getConversation(conversationId: Long): ChatConversationEntity? {
        return chatDao.getConversationById(conversationId)
    }
    
    /**
     * Delete a conversation
     */
    suspend fun deleteConversation(conversationId: Long) {
        chatDao.deleteConversationById(conversationId)
    }
    
    /**
     * Generate a title from the first message
     */
    private fun generateTitle(message: String): String {
        return if (message.length > 50) {
            message.take(47) + "..."
        } else {
            message
        }
    }
}
