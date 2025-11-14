package com.unnikrishnan.dataclerk.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unnikrishnan.dataclerk.data.models.*
import com.unnikrishnan.dataclerk.data.repository.ChatRepository
import com.unnikrishnan.dataclerk.data.repository.MessageContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Chat Screen
 */
class ChatViewModel : ViewModel() {
    
    private val chatRepository = ChatRepository()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    private val _geminiApiKey = MutableStateFlow<String?>(null)
    val geminiApiKey: StateFlow<String?> = _geminiApiKey.asStateFlow()
    
    private var currentDatabaseName: String = ""
    
    fun initialize(databaseName: String, apiKey: String?) {
        currentDatabaseName = databaseName
        _geminiApiKey.value = apiKey
        
        // Add welcome message
        if (_messages.value.isEmpty()) {
            addMessage(
                ChatMessage(
                    content = "Hello! I'm your database assistant for **$databaseName**. Ask me anything about your data, and I'll help you analyze it.",
                    role = MessageRole.ASSISTANT,
                    contentType = MessageContentType.TEXT
                )
            )
        }
    }
    
    fun setGeminiApiKey(apiKey: String) {
        _geminiApiKey.value = apiKey
    }
    
    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank() || _isProcessing.value) return
        
        val apiKey = _geminiApiKey.value
        if (apiKey.isNullOrBlank()) {
            addMessage(
                ChatMessage(
                    content = "⚠️ Gemini API key not configured. Please add your API key in Settings.",
                    role = MessageRole.ASSISTANT,
                    contentType = MessageContentType.ERROR
                )
            )
            return
        }
        
        // Add user message
        addMessage(
            ChatMessage(
                content = userPrompt,
                role = MessageRole.USER,
                contentType = MessageContentType.TEXT
            )
        )
        
        // Process query
        processUserQuery(userPrompt, apiKey)
    }
    
    private fun processUserQuery(userPrompt: String, apiKey: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            
            try {
                val result = chatRepository.processUserQuery(
                    databaseName = currentDatabaseName,
                    userPrompt = userPrompt,
                    geminiApiKey = apiKey
                )
                
                if (result.isSuccess) {
                    val response = result.getOrNull()!!
                    handleSuccessfulResponse(response)
                } else {
                    handleError(result.exceptionOrNull()?.message ?: "Unknown error occurred")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "An unexpected error occurred")
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    private fun handleSuccessfulResponse(response: com.unnikrishnan.dataclerk.data.repository.ChatResponse) {
        // Add messages based on formatted response
        response.formattedResponse.messages.forEach { content ->
            when (content) {
                is MessageContent.Text -> {
                    addMessage(
                        ChatMessage(
                            content = content.content,
                            role = MessageRole.ASSISTANT,
                            contentType = MessageContentType.TEXT,
                            metadata = MessageMetadata(
                                query = response.sqlQuery,
                                rowCount = response.queryResults.size,
                                executionTime = response.executionTime
                            )
                        )
                    )
                }
                is MessageContent.Table -> {
                    addMessage(
                        ChatMessage(
                            content = content.description,
                            role = MessageRole.ASSISTANT,
                            contentType = MessageContentType.TABLE,
                            tableData = TableData(
                                headers = content.headers,
                                rows = content.rows
                            ),
                            metadata = MessageMetadata(
                                query = response.sqlQuery,
                                rowCount = response.queryResults.size,
                                executionTime = response.executionTime
                            )
                        )
                    )
                }
                is MessageContent.Chart -> {
                    addMessage(
                        ChatMessage(
                            content = content.description,
                            role = MessageRole.ASSISTANT,
                            contentType = MessageContentType.CHART,
                            chartData = ChartData(
                                type = when (content.type) {
                                    com.unnikrishnan.dataclerk.data.repository.ChartType.BAR -> ChartType.BAR
                                    com.unnikrishnan.dataclerk.data.repository.ChartType.LINE -> ChartType.LINE
                                    com.unnikrishnan.dataclerk.data.repository.ChartType.PIE -> ChartType.PIE
                                },
                                labels = content.labels,
                                values = content.values
                            ),
                            metadata = MessageMetadata(
                                query = response.sqlQuery,
                                rowCount = response.queryResults.size,
                                executionTime = response.executionTime
                            )
                        )
                    )
                }
            }
        }
    }
    
    private fun handleError(errorMessage: String) {
        addMessage(
            ChatMessage(
                content = "❌ Error: $errorMessage",
                role = MessageRole.ASSISTANT,
                contentType = MessageContentType.ERROR,
                metadata = MessageMetadata(error = errorMessage)
            )
        )
    }
    
    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
    
    fun clearChat() {
        _messages.value = emptyList()
        initialize(currentDatabaseName, _geminiApiKey.value)
    }
}
