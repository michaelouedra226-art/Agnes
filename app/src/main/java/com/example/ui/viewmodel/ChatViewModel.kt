package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AtelierDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.MediaType
import com.example.data.model.MessageRole
import com.example.service.AgnesApiClient
import com.example.service.ApiKeyManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AtelierDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val mediaDao = db.mediaDao()
    private val keyManager = ApiKeyManager(application)
    private val agnesClient = AgnesApiClient(keyManager)

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _activeMode = MutableStateFlow("Chat")
    val activeMode: StateFlow<String> = _activeMode.asStateFlow()

    private var currentStreamingJob: Job? = null

    fun setMode(mode: String) {
        _activeMode.value = mode
    }

    fun sendMessage(conversationId: String, text: String) {
        val prompt = text.trim()
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            val userMsg = ChatMessage(
                conversationId = conversationId,
                role = MessageRole.USER,
                content = prompt
            )
            chatDao.insertMessage(userMsg)

            val assistantMsgId = UUID.randomUUID().toString()
            val placeholderAssistantMsg = ChatMessage(
                id = assistantMsgId,
                conversationId = conversationId,
                role = MessageRole.ASSISTANT,
                content = "",
                isStreaming = true
            )
            chatDao.insertMessage(placeholderAssistantMsg)

            _isStreaming.value = true
            currentStreamingJob = launch {
                val sb = StringBuilder()
                try {
                    agnesClient.streamChat(prompt).collect { chunk ->
                        sb.append(chunk)
                        chatDao.updateMessage(
                            placeholderAssistantMsg.copy(
                                content = sb.toString(),
                                isStreaming = true
                            )
                        )
                    }
                } finally {
                    _isStreaming.value = false
                    chatDao.updateMessage(
                        placeholderAssistantMsg.copy(
                            content = sb.toString(),
                            isStreaming = false
                        )
                    )
                }
            }
        }
    }

    fun generateImageInline(conversationId: String, prompt: String) {
        viewModelScope.launch {
            chatDao.insertMessage(
                ChatMessage(
                    conversationId = conversationId,
                    role = MessageRole.USER,
                    content = "Générer image : $prompt"
                )
            )
            try {
                val url = agnesClient.generateImage(prompt)
                chatDao.insertMessage(
                    ChatMessage(
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = "Rendu visuel généré : \"$prompt\"",
                        mediaType = MediaType.IMAGE,
                        mediaUrl = url,
                        prompt = prompt
                    )
                )
                mediaDao.insertMedia(
                    com.example.data.model.MediaItem(
                        title = "Image - ${prompt.take(15)}",
                        prompt = prompt,
                        type = MediaType.IMAGE,
                        url = url
                    )
                )
            } catch (e: Exception) {
                chatDao.insertMessage(
                    ChatMessage(
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = "Erreur de génération d'image : ${e.localizedMessage}"
                    )
                )
            }
        }
    }

    fun generateVideoInline(conversationId: String, prompt: String) {
        viewModelScope.launch {
            chatDao.insertMessage(
                ChatMessage(
                    conversationId = conversationId,
                    role = MessageRole.USER,
                    content = "Générer vidéo : $prompt"
                )
            )
            try {
                val url = agnesClient.submitVideoJob(prompt)
                chatDao.insertMessage(
                    ChatMessage(
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = "Séquence vidéo générée : \"$prompt\"",
                        mediaType = MediaType.VIDEO,
                        mediaUrl = url,
                        prompt = prompt
                    )
                )
                mediaDao.insertMedia(
                    com.example.data.model.MediaItem(
                        title = "Vidéo - ${prompt.take(15)}",
                        prompt = prompt,
                        type = MediaType.VIDEO,
                        url = url
                    )
                )
            } catch (e: Exception) {
                chatDao.insertMessage(
                    ChatMessage(
                        conversationId = conversationId,
                        role = MessageRole.ASSISTANT,
                        content = "Erreur de génération vidéo : ${e.localizedMessage}"
                    )
                )
            }
        }
    }

    fun stopStreaming() {
        currentStreamingJob?.cancel()
        _isStreaming.value = false
    }

    fun clearHistory(conversationId: String) {
        viewModelScope.launch {
            chatDao.deleteMessagesForConvo(conversationId)
        }
    }
}
