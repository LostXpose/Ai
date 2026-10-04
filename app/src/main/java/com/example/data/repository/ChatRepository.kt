package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {
  val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()
  val totalMessagesCount: Flow<Int> = chatDao.getTotalMessagesCount()

  fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> {
    return chatDao.getMessagesForSession(sessionId)
  }

  suspend fun createNewSession(
    modelId: String,
    modelName: String,
    isCloudMode: Boolean = false,
    title: String = "New Offline Chat"
  ): String = withContext(Dispatchers.IO) {
    val newId = UUID.randomUUID().toString()
    val session = ChatSessionEntity(
      id = newId,
      title = title,
      modelId = modelId,
      modelName = modelName,
      isCloudMode = isCloudMode,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )
    chatDao.insertSession(session)
    newId
  }

  suspend fun saveMessage(
    sessionId: String,
    role: String,
    content: String,
    tokensCount: Int = 0,
    tokPerSec: Double = 0.0,
    latencyMs: Long = 0L,
    modelName: String = "",
    isError: Boolean = false
  ): String = withContext(Dispatchers.IO) {
    val msgId = UUID.randomUUID().toString()
    val msg = ChatMessageEntity(
      id = msgId,
      sessionId = sessionId,
      role = role,
      content = content,
      timestamp = System.currentTimeMillis(),
      tokensCount = tokensCount,
      tokPerSec = tokPerSec,
      latencyMs = latencyMs,
      modelName = modelName,
      isError = isError
    )
    chatDao.insertMessage(msg)

    // Update session timestamp & auto-name title if it's the first user message
    val session = chatDao.getSessionById(sessionId)
    if (session != null) {
      var updatedTitle = session.title
      if (role == "user" && (session.title == "New Offline Chat" || session.title.startsWith("New Chat"))) {
        updatedTitle = content.take(32).trim()
        if (content.length > 32) updatedTitle += "..."
      }
      chatDao.updateSession(
        session.copy(
          title = updatedTitle,
          updatedAt = System.currentTimeMillis()
        )
      )
    }
    msgId
  }

  suspend fun deleteSession(sessionId: String) = withContext(Dispatchers.IO) {
    chatDao.deleteSessionById(sessionId)
  }

  suspend fun clearSession(sessionId: String) = withContext(Dispatchers.IO) {
    chatDao.clearSessionMessages(sessionId)
  }

  suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
    chatDao.deleteAllSessions()
  }

  suspend fun exportSessionAsMarkdown(sessionId: String): String = withContext(Dispatchers.IO) {
    val session = chatDao.getSessionById(sessionId) ?: return@withContext ""
    val messages = chatDao.getMessagesListForSession(sessionId)
    val sb = StringBuilder()
    sb.append("# NeonLLM Chat Export: ${session.title}\n")
    sb.append("**Model**: ${session.modelName} | **Mode**: ${if (session.isCloudMode) "Cloud API" else "Local Offline (No Token)"}\n\n---\n\n")
    for (m in messages) {
      val roleHeader = when (m.role) {
        "user" -> "### 👤 User"
        "assistant" -> "### ⚡ NeonLLM (${m.modelName} • ${String.format("%.1f", m.tokPerSec)} tok/s)"
        else -> "### ⚙️ System"
      }
      sb.append("$roleHeader\n${m.content}\n\n")
    }
    sb.toString()
  }
}
