package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "chat_messages",
  foreignKeys = [
    ForeignKey(
      entity = ChatSessionEntity::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["sessionId"])]
)
data class ChatMessageEntity(
  @PrimaryKey val id: String,
  val sessionId: String,
  val role: String, // "user", "assistant", "system"
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val tokensCount: Int = 0,
  val tokPerSec: Double = 0.0,
  val latencyMs: Long = 0L,
  val modelName: String = "",
  val isError: Boolean = false
)
