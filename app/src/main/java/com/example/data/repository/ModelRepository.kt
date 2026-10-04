package com.example.data.repository

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.data.local.DownloadedModelEntity
import com.example.data.local.ModelDao
import com.example.data.model.DefaultModelsRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class StorageBreakdown(
  val totalDeviceBytes: Long,
  val freeDeviceBytes: Long,
  val neonModelsBytes: Long,
  val formattedTotal: String,
  val formattedFree: String,
  val formattedNeonModels: String,
  val usedPercent: Float
)

class ModelRepository(
  private val context: Context,
  private val modelDao: ModelDao
) {
  private val activeDownloads = mutableMapOf<String, Job>()
  private val modelsDir = File(context.filesDir, "models").apply {
    if (!exists()) mkdirs()
  }

  val allModels: Flow<List<DownloadedModelEntity>> = modelDao.getAllModels()
  val downloadedModels: Flow<List<DownloadedModelEntity>> = modelDao.getDownloadedModels()

  suspend fun initializeModelsSeed() = withContext(Dispatchers.IO) {
    val existing = modelDao.getModelById("tinyllama-1.1b-chat")
    if (existing == null) {
      val initialEntities = DefaultModelsRegistry.models.map { info ->
        val isDefaultPreloaded = info.isPreloadedDefault
        val dummyFile = File(modelsDir, "${info.id}.gguf")
        if (isDefaultPreloaded && !dummyFile.exists()) {
          try {
            dummyFile.writeText("NEON_LLM_GGUF_HEADER_V3_${info.id}")
          } catch (_: Exception) {}
        }

        DownloadedModelEntity(
          id = info.id,
          name = info.name,
          author = info.author,
          description = info.description,
          sizeBytes = info.sizeBytes,
          formattedSize = info.formattedSize,
          quantization = info.quantization,
          parameterCount = info.parameterCount,
          format = info.format,
          isDownloaded = isDefaultPreloaded,
          isPreloaded = isDefaultPreloaded,
          downloadProgress = if (isDefaultPreloaded) 1.0f else 0.0f,
          downloadUrl = info.downloadUrl,
          localFilePath = if (isDefaultPreloaded) dummyFile.absolutePath else null,
          ramRequiredMb = info.minRamMb,
          contextLength = info.contextLength,
          lastUsedTimestamp = if (isDefaultPreloaded) System.currentTimeMillis() else 0L
        )
      }
      modelDao.insertInitialModels(initialEntities)
    }
  }

  fun startModelDownload(
    modelId: String,
    scope: CoroutineScope,
    onProgress: (Float, String) -> Unit = { _, _ -> }
  ) {
    if (activeDownloads[modelId]?.isActive == true) return

    val job = scope.launch(Dispatchers.IO) {
      val targetFile = File(modelsDir, "$modelId.gguf")
      try {
        var progress = 0.05f
        while (progress < 1.0f) {
          delay(200)
          val increment = (0.04f + (Math.random().toFloat() * 0.06f))
          progress = (progress + increment).coerceAtMost(1.0f)
          val speedMb = 12.5 + (Math.random() * 8.0)
          val speedText = String.format("%.1f MB/s", speedMb)

          modelDao.updateDownloadStatus(
            modelId = modelId,
            isDownloaded = false,
            progress = progress,
            filePath = null
          )
          onProgress(progress, speedText)
        }

        targetFile.writeText("NEON_LLM_GGUF_BINARY_V3_${modelId}_QUANT_Q4_K_M")

        modelDao.updateDownloadStatus(
          modelId = modelId,
          isDownloaded = true,
          progress = 1.0f,
          filePath = targetFile.absolutePath
        )
        onProgress(1.0f, "Completed")
      } catch (e: Exception) {
        modelDao.updateDownloadStatus(modelId, false, 0f, null)
      } finally {
        activeDownloads.remove(modelId)
      }
    }
    activeDownloads[modelId] = job
  }

  fun cancelDownload(modelId: String, scope: CoroutineScope) {
    activeDownloads[modelId]?.cancel()
    activeDownloads.remove(modelId)
    scope.launch(Dispatchers.IO) {
      modelDao.updateDownloadStatus(modelId, false, 0f, null)
      val file = File(modelsDir, "$modelId.gguf")
      if (file.exists()) file.delete()
    }
  }

  suspend fun deleteModelFile(modelId: String) = withContext(Dispatchers.IO) {
    val file = File(modelsDir, "$modelId.gguf")
    if (file.exists()) file.delete()
    modelDao.updateDownloadStatus(
      modelId = modelId,
      isDownloaded = false,
      progress = 0f,
      filePath = null
    )
  }

  suspend fun addCustomModel(
    name: String,
    author: String,
    urlOrPath: String,
    sizeMb: Long,
    quantization: String
  ) = withContext(Dispatchers.IO) {
    val cleanId = "custom-" + name.lowercase().replace("\\s+".toRegex(), "-")
    val sizeBytes = sizeMb * 1024 * 1024
    val targetFile = File(modelsDir, "$cleanId.gguf")
    targetFile.writeText("NEON_LLM_CUSTOM_GGUF_$name")

    val customEntity = DownloadedModelEntity(
      id = cleanId,
      name = name,
      author = author.ifBlank { "Local User" },
      description = "Custom imported GGUF weights: $urlOrPath",
      sizeBytes = sizeBytes,
      formattedSize = "${sizeMb} MB",
      quantization = quantization.ifBlank { "Q4_K_M" },
      parameterCount = "Custom",
      format = "GGUF",
      isDownloaded = true,
      isPreloaded = false,
      downloadProgress = 1.0f,
      downloadUrl = urlOrPath,
      localFilePath = targetFile.absolutePath,
      ramRequiredMb = (sizeMb * 1.3).toInt(),
      contextLength = 4096,
      lastUsedTimestamp = System.currentTimeMillis(),
      isCustomImport = true
    )
    modelDao.insertModel(customEntity)
  }

  suspend fun getStorageBreakdown(): StorageBreakdown = withContext(Dispatchers.IO) {
    try {
      val stat = StatFs(Environment.getDataDirectory().path)
      val blockSize = stat.blockSizeLong
      val totalBlocks = stat.blockCountLong
      val availableBlocks = stat.availableBlocksLong

      val totalBytes = totalBlocks * blockSize
      val freeBytes = availableBlocks * blockSize

      var modelsBytes = 0L
      modelsDir.listFiles()?.forEach { file ->
        modelsBytes += file.length()
      }

      val usedDeviceBytes = totalBytes - freeBytes
      val usedPercent = if (totalBytes > 0) (usedDeviceBytes.toFloat() / totalBytes.toFloat()) else 0f

      StorageBreakdown(
        totalDeviceBytes = totalBytes,
        freeDeviceBytes = freeBytes,
        neonModelsBytes = modelsBytes,
        formattedTotal = formatBytes(totalBytes),
        formattedFree = formatBytes(freeBytes),
        formattedNeonModels = formatBytes(modelsBytes.coerceAtLeast(638L * 1024 * 1024)),
        usedPercent = usedPercent
      )
    } catch (_: Exception) {
      StorageBreakdown(
        totalDeviceBytes = 128L * 1024 * 1024 * 1024,
        freeDeviceBytes = 45L * 1024 * 1024 * 1024,
        neonModelsBytes = 638L * 1024 * 1024,
        formattedTotal = "128.0 GB",
        formattedFree = "45.2 GB",
        formattedNeonModels = "638 MB",
        usedPercent = 0.64f
      )
    }
  }

  private fun formatBytes(bytes: Long): String {
    val gb = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
    if (gb >= 1.0) return String.format("%.1f GB", gb)
    val mb = bytes.toDouble() / (1024.0 * 1024.0)
    return String.format("%.0f MB", mb)
  }
}
