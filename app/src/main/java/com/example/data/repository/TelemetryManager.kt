package com.example.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.data.model.InferenceMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TelemetryManager(private val context: Context) {
  private val _metrics = MutableStateFlow(InferenceMetrics())
  val metrics: StateFlow<InferenceMetrics> = _metrics.asStateFlow()

  fun sampleHardwareTelemetry() {
    try {
      val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
      val memInfo = ActivityManager.MemoryInfo()
      actManager?.getMemoryInfo(memInfo)

      val totalRamMb = (memInfo.totalMem / (1024 * 1024))
      val freeRamMb = (memInfo.availMem / (1024 * 1024))
      val usedRamMb = (totalRamMb - freeRamMb).coerceAtLeast(0L)

      val batteryIntent = context.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
      )
      val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 280) ?: 280
      val batteryTemp = rawTemp / 10.0f
      val batteryLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 80) ?: 80

      val pressure = when {
        memInfo.lowMemory -> "Critical"
        freeRamMb < 800 -> "Moderate"
        else -> "Normal"
      }

      val availableCores = Runtime.getRuntime().availableProcessors()

      _metrics.update { current ->
        current.copy(
          ramTotalMb = totalRamMb,
          ramUsedMb = usedRamMb,
          ramFreeMb = freeRamMb,
          batteryTempCelsius = batteryTemp,
          batteryPercentage = batteryLevel,
          memoryPressure = pressure,
          activeThreads = availableCores.coerceAtMost(current.activeThreads.coerceAtLeast(2))
        )
      }
    } catch (_: Exception) {
      // Fallback grace
    }
  }

  fun updateInferenceLiveStats(
    tokPerSec: Double,
    ttftMs: Long,
    tokensGenerated: Int,
    contextTokens: Int,
    maxContext: Int,
    status: String,
    isGenerating: Boolean
  ) {
    _metrics.update { current ->
      current.copy(
        tokensPerSecond = tokPerSec,
        timeToFirstTokenMs = ttftMs,
        totalTokensGenerated = tokensGenerated,
        totalTokensInContext = contextTokens,
        maxContextTokens = maxContext,
        engineStatus = status,
        isGenerating = isGenerating
      )
    }
  }
}
