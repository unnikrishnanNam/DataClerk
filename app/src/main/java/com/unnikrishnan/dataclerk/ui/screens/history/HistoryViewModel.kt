package com.unnikrishnan.dataclerk.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unnikrishnan.dataclerk.data.local.ChatConversationEntity
import com.unnikrishnan.dataclerk.data.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val chatHistoryRepository = ChatHistoryRepository(application)
    
    private val _conversations = MutableStateFlow<List<ChatConversationEntity>>(emptyList())
    val conversations: StateFlow<List<ChatConversationEntity>> = _conversations.asStateFlow()
    
    fun loadConversations(databaseName: String) {
        viewModelScope.launch {
            chatHistoryRepository.getConversationsByDatabase(databaseName)
                .collect { conversationList ->
                    _conversations.value = conversationList
                }
        }
    }
    
    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            chatHistoryRepository.deleteConversation(conversationId)
        }
    }
}
