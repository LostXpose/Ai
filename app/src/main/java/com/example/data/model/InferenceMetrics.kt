package com.example.data.model

data class InferenceMetrics(
  val tokensPerSecond: Double = 0.0,
  val timeToFirstTokenMs: Long = 0L,
  val totalTokensGenerated: Int = 0,
  val totalTokensInContext: Int = 0,
  val maxContextTokens: Int = 4096,
  val ramUsedMb: Long = 0L,
  val ramTotalMb: Long = 0L,
  val ramFreeMb: Long = 0L,
  val batteryTempCelsius: Float = 28.5f,
  val batteryPercentage: Int = 85,
  val activeThreads: Int = 4,
  val engineStatus: String = "Ready (Offline Local)",
  val isGenerating: Boolean = false,
  val memoryPressure: String = "Normal" // "Normal", "Moderate", "Critical"
)
