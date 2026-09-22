package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convoId ORDER BY timestamp ASC")
    fun getMessages(convoId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE conversationId = :convoId")
    suspend fun deleteMessagesForConvo(convoId: String)

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getConversations(): Flow<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(convo: Conversation)

    @Delete
    suspend fun deleteConversation(convo: Conversation)
}

@Dao
interface BatchDao {
    @Query("SELECT * FROM batch_jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<BatchJob>>

    @Query("SELECT * FROM batch_jobs WHERE status IN ('PENDING', 'PROCESSING') ORDER BY createdAt ASC")
    suspend fun getActiveJobs(): List<BatchJob>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<BatchJob>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: BatchJob)

    @Update
    suspend fun updateJob(job: BatchJob)

    @Query("UPDATE batch_jobs SET status = :status WHERE id = :id")
    suspend fun updateJobStatus(id: String, status: JobStatus)

    @Query("DELETE FROM batch_jobs WHERE id = :id")
    suspend fun deleteJob(id: String)

    @Query("DELETE FROM batch_jobs")
    suspend fun clearAll()
}

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY createdAt DESC")
    fun getAllMedia(): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItem)

    @Delete
    suspend fun deleteMedia(media: MediaItem)
}

@Dao
interface ApiKeyDao {
    @Query("SELECT * FROM api_keys ORDER BY lastTestedAt DESC")
    fun getAllKeys(): Flow<List<ApiKeyEntity>>

    @Query("SELECT * FROM api_keys WHERE isValid = 1 AND rateLimited = 0")
    suspend fun getWorkingKeys(): List<ApiKeyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: ApiKeyEntity)

    @Delete
    suspend fun deleteKey(key: ApiKeyEntity)

    @Update
    suspend fun updateKey(key: ApiKeyEntity)
}
