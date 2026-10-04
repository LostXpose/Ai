package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_models")
data class DownloadedModelEntity(
  @PrimaryKey val id: String,
  val name: String,
  val author: String,
  val description: String,
  val sizeBytes: Long,
  val formattedSize: String,
  val quantization: String,
  val parameterCount: String,
  val format: String = "GGUF",
  val isDownloaded: Boolean = false,
  val isPreloaded: Boolean = false,
  val downloadProgress: Float = 0f,
  val downloadUrl: String = "",
  val localFilePath: String? = null,
  val ramRequiredMb: Int = 1024,
  val contextLength: Int = 4096,
  val lastUsedTimestamp: Long = 0L,
  val isCustomImport: Boolean = false
)
