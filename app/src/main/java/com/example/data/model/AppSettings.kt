package com.example.data.model

data class AppSettings(
  val themeMode: ThemeMode = ThemeMode.NEON_CYBER,
  val activeModelId: String = "tinyllama-1.1b-chat",
  val isCloudApiMode: Boolean = false,
  val geminiApiKey: String = "",
  val customApiEndpoint: String = "https://generativelanguage.googleapis.com/",
  val temperature: Float = 0.7f,
  val topP: Float = 0.9f,
  val maxTokens: Int = 1024,
  val cpuThreads: Int = 4,
  val systemPrompt: String = "You are NeonLLM, an intelligent, fast, and completely offline AI assistant. Answer clearly, accurately, and politely in the user's language.",
  val showLiveTelemetry: Boolean = true,
  val streamTypingSpeedMs: Long = 18L
)
