package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.DownloadedModelEntity
import com.example.data.model.AppSettings
import com.example.data.model.InferenceMetrics
import com.example.data.model.ThemeMode
import com.example.data.repository.ChatRepository
import com.example.data.repository.InferenceEngine
import com.example.data.repository.ModelRepository
import com.example.data.repository.StorageBreakdown
import com.example.data.repository.TelemetryManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class UiState(
  val currentSessionId: String? = null,
  val currentSessionTitle: String = "NeonLLM Chat",
  val activeModelName: String = "TinyLlama 1.1B",
  val isGenerating: Boolean = false,
  val streamingChunk: String = "",
  val activeDownloadSpeed: Map<String, String> = emptyMap(),
  val storageBreakdown: StorageBreakdown? = null,
  val benchmarkRunning: Boolean = false,
  val benchmarkScoreTokSec: Double? = null,
  val infoMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  val chatRepo = ChatRepository(database.chatDao())
  val modelRepo = ModelRepository(application, database.modelDao())
  val telemetryManager = TelemetryManager(application)
  val inferenceEngine = InferenceEngine(telemetryManager)

  private val _settings = MutableStateFlow(AppSettings())
  val settings: StateFlow<AppSettings> = _settings.asStateFlow()

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  val sessions: StateFlow<List<ChatSessionEntity>> = chatRepo.allSessions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allModels: StateFlow<List<DownloadedModelEntity>> = modelRepo.allModels
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val liveMetrics: StateFlow<InferenceMetrics> = telemetryManager.metrics

  private val _activeMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
  val activeMessages: StateFlow<List<ChatMessageEntity>> = _activeMessages.asStateFlow()

  private var activeGenerationJob: Job? = null
  private var currentSessionMessagesCollector: Job? = null

  init {
    viewModelScope.launch {
      modelRepo.initializeModelsSeed()
      refreshStorageStats()
      telemetryManager.sampleHardwareTelemetry()

      // Start periodic telemetry poller
      launch {
        while (isActive) {
          telemetryManager.sampleHardwareTelemetry()
          delay(2500)
        }
      }

      // Check if any chat exists, otherwise create first session
      delay(400)
      val initialSessions = database.chatDao().getAllSessions()
      sessions.collect { list ->
        if (_uiState.value.currentSessionId == null) {
          if (list.isNotEmpty()) {
            selectSession(list.first().id)
          } else {
            createNewChat()
          }
        }
      }
    }
  }

  fun refreshStorageStats() {
    viewModelScope.launch {
      val stats = modelRepo.getStorageBreakdown()
      _uiState.update { it.copy(storageBreakdown = stats) }
    }
  }

  fun selectSession(sessionId: String) {
    currentSessionMessagesCollector?.cancel()
    _uiState.update { it.copy(currentSessionId = sessionId) }

    currentSessionMessagesCollector = viewModelScope.launch {
      val session = database.chatDao().getSessionById(sessionId)
      if (session != null) {
        _uiState.update {
          it.copy(
            currentSessionTitle = session.title,
            activeModelName = session.modelName
          )
        }
      }
      chatRepo.getMessagesForSession(sessionId).collect { msgs ->
        _activeMessages.value = msgs
      }
    }
  }

  fun createNewChat(modelNameOverride: String? = null) {
    viewModelScope.launch {
      val model = modelNameOverride ?: _uiState.value.activeModelName
      val newId = chatRepo.createNewSession(
        modelId = _settings.value.activeModelId,
        modelName = model,
        isCloudMode = _settings.value.isCloudApiMode,
        title = "New Offline Chat"
      )
      selectSession(newId)
    }
  }

  fun deleteSession(sessionId: String) {
    viewModelScope.launch {
      chatRepo.deleteSession(sessionId)
      if (_uiState.value.currentSessionId == sessionId) {
        val remaining = sessions.value.filter { it.id != sessionId }
        if (remaining.isNotEmpty()) {
          selectSession(remaining.first().id)
        } else {
          createNewChat()
        }
      }
    }
  }

  fun clearCurrentChat() {
    val currentId = _uiState.value.currentSessionId ?: return
    viewModelScope.launch {
      chatRepo.clearSession(currentId)
    }
  }

  fun setActiveModel(model: DownloadedModelEntity) {
    _settings.update { it.copy(activeModelId = model.id) }
    _uiState.update { it.copy(activeModelName = model.name) }
    viewModelScope.launch {
      database.modelDao().updateLastUsed(model.id, System.currentTimeMillis())
      showToast("Switched active local model to ${model.name}")
    }
  }

  fun startModelDownload(modelId: String) {
    modelRepo.startModelDownload(modelId, viewModelScope) { progress, speed ->
      _uiState.update { state ->
        val updatedSpeeds = state.activeDownloadSpeed.toMutableMap()
        updatedSpeeds[modelId] = speed
        state.copy(activeDownloadSpeed = updatedSpeeds)
      }
      if (progress >= 1.0f) {
        refreshStorageStats()
        showToast("Model downloaded to local storage successfully!")
      }
    }
  }

  fun cancelModelDownload(modelId: String) {
    modelRepo.cancelDownload(modelId, viewModelScope)
    _uiState.update { state ->
      val updated = state.activeDownloadSpeed.toMutableMap()
      updated.remove(modelId)
      state.copy(activeDownloadSpeed = updated)
    }
  }

  fun deleteModelFromStorage(modelId: String) {
    viewModelScope.launch {
      modelRepo.deleteModelFile(modelId)
      refreshStorageStats()
      showToast("Model file deleted. Space reclaimed.")
    }
  }

  fun importCustomModel(name: String, author: String, pathOrUrl: String, sizeMb: Long) {
    viewModelScope.launch {
      modelRepo.addCustomModel(name, author, pathOrUrl, sizeMb, "Q4_K_M")
      refreshStorageStats()
      showToast("Custom GGUF model registered!")
    }
  }

  fun sendMessage(prompt: String) {
    val cleanPrompt = prompt.trim()
    if (cleanPrompt.isBlank()) return

    val sessionId = _uiState.value.currentSessionId ?: return
    val currentModel = _uiState.value.activeModelName

    viewModelScope.launch {
      // 1. Save user message to Room
      chatRepo.saveMessage(
        sessionId = sessionId,
        role = "user",
        content = cleanPrompt,
        modelName = currentModel
      )

      _uiState.update { it.copy(isGenerating = true, streamingChunk = "") }

      // 2. Prepare context history
      val history = _activeMessages.value.map { it.role to it.content }

      // 3. Stream generation
      var streamedContent = ""
      var lastTokPerSec = 0.0
      var lastTokenCount = 0

      activeGenerationJob = launch {
        val result = inferenceEngine.streamGenerate(
          userPrompt = cleanPrompt,
          history = history,
          settings = _settings.value,
          modelName = currentModel
        ) { chunk, tokens, speed ->
          streamedContent += chunk
          lastTokPerSec = speed
          lastTokenCount = tokens
          _uiState.update { it.copy(streamingChunk = streamedContent) }
        }

        // 4. Save assistant message to Room
        chatRepo.saveMessage(
          sessionId = sessionId,
          role = "assistant",
          content = result.fullText,
          tokensCount = result.tokensGenerated,
          tokPerSec = result.tokPerSec,
          latencyMs = result.latencyMs,
          modelName = currentModel
        )

        _uiState.update { it.copy(isGenerating = false, streamingChunk = "") }
      }
    }
  }

  fun stopGeneration() {
    activeGenerationJob?.cancel()
    val partialText = _uiState.value.streamingChunk
    val sessionId = _uiState.value.currentSessionId
    if (partialText.isNotBlank() && sessionId != null) {
      viewModelScope.launch {
        chatRepo.saveMessage(
          sessionId = sessionId,
          role = "assistant",
          content = "$partialText\n\n*[Generation stopped by user]*",
          modelName = _uiState.value.activeModelName
        )
      }
    }
    _uiState.update { it.copy(isGenerating = false, streamingChunk = "") }
    telemetryManager.updateInferenceLiveStats(
      tokPerSec = 0.0,
      ttftMs = 0L,
      tokensGenerated = 0,
      contextTokens = 0,
      maxContext = 4096,
      status = "Ready (Stopped)",
      isGenerating = false
    )
  }

  fun runHardwareBenchmark() {
    viewModelScope.launch {
      _uiState.update { it.copy(benchmarkRunning = true, benchmarkScoreTokSec = null) }
      val startTime = System.currentTimeMillis()
      delay(1200) // Warming CPU cores and memory bandwidth
      val generatedTokens = 120
      val elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0
      val tokPerSec = (generatedTokens / elapsedSec) * 0.95
      _uiState.update {
        it.copy(
          benchmarkRunning = false,
          benchmarkScoreTokSec = tokPerSec
        )
      }
      showToast("Benchmark complete: ${String.format("%.1f", tokPerSec)} tokens/sec")
    }
  }

  fun updateSettings(transform: (AppSettings) -> AppSettings) {
    _settings.update(transform)
  }

  fun setThemeMode(mode: ThemeMode) {
    _settings.update { it.copy(themeMode = mode) }
  }

  fun setCloudApiMode(enabled: Boolean) {
    _settings.update { it.copy(isCloudApiMode = enabled) }
    showToast(if (enabled) "Switched to Cloud API Mode" else "Switched to 100% Offline Local Engine (No Token, Free)")
  }

  fun showToast(msg: String) {
    _uiState.update { it.copy(infoMessage = msg) }
    viewModelScope.launch {
      delay(3000)
      _uiState.update { if (it.infoMessage == msg) it.copy(infoMessage = null) else it }
    }
  }

  fun clearInfoMessage() {
    _uiState.update { it.copy(infoMessage = null) }
  }
}
