package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

enum class MediaType {
    NONE, IMAGE, VIDEO
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String = "default_session",
    val role: MessageRole = MessageRole.USER,
    val content: String = "",
    val mediaType: MediaType = MediaType.NONE,
    val mediaUrl: String? = null,
    val prompt: String? = null,
    val isStreaming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "Nouvelle Session",
    val updatedAt: Long = System.currentTimeMillis(),
    val projectTag: String = "Général"
)

enum class JobStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, PAUSED
}

@Entity(tableName = "batch_jobs")
data class BatchJob(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val model: String = "Agnes-Video-v2",
    val status: JobStatus = JobStatus.PENDING,
    val progress: Float = 0f,
    val resultUrl: String? = null,
    val error: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val retries: Int = 0
)

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val label: String,
    val provider: String = "Agnes", // Agnes, OpenAI, Anthropic, Gemini, Custom
    val baseUrl: String = "https://api.agnes.ai/v1",
    val keyPreview: String,
    val encryptedKey: String,
    val isValid: Boolean = true,
    val rateLimited: Boolean = false,
    val usageCount: Int = 0,
    val lastTestedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val type: MediaType,
    val url: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
