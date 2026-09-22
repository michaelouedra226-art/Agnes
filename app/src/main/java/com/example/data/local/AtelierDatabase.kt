package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        ChatMessage::class,
        Conversation::class,
        BatchJob::class,
        ApiKeyEntity::class,
        MediaItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AtelierDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun batchDao(): BatchDao
    abstract fun mediaDao(): MediaDao
    abstract fun apiKeyDao(): ApiKeyDao

    companion object {
        @Volatile
        private var INSTANCE: AtelierDatabase? = null

        fun getDatabase(context: Context): AtelierDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtelierDatabase::class.java,
                    "atelier_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
