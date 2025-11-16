package com.unnikrishnan.dataclerk.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    
    // Conversations
    @Insert
    suspend fun insertConversation(conversation: ChatConversationEntity): Long
    
    @Update
    suspend fun updateConversation(conversation: ChatConversationEntity)
    
    @Delete
    suspend fun deleteConversation(conversation: ChatConversationEntity)
    
    @Query("SELECT * FROM chat_conversations WHERE databaseName = :databaseName ORDER BY updatedAt DESC")
    fun getConversationsByDatabase(databaseName: String): Flow<List<ChatConversationEntity>>
    
    @Query("SELECT * FROM chat_conversations ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentConversations(limit: Int = 10): Flow<List<ChatConversationEntity>>
    
    @Query("SELECT * FROM chat_conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: Long): ChatConversationEntity?
    
    @Query("DELETE FROM chat_conversations WHERE id = :conversationId")
    suspend fun deleteConversationById(conversationId: Long)
    
    // Messages
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long
    
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesByConversation(conversationId: Long): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesByConversationSync(conversationId: Long): List<ChatMessageEntity>
    
    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: Long)
    
    @Query("SELECT COUNT(*) FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun getMessageCount(conversationId: Long): Int
}
