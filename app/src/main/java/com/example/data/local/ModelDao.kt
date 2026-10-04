package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
  @Query("SELECT * FROM downloaded_models ORDER BY lastUsedTimestamp DESC, name ASC")
  fun getAllModels(): Flow<List<DownloadedModelEntity>>

  @Query("SELECT * FROM downloaded_models WHERE isDownloaded = 1 ORDER BY lastUsedTimestamp DESC")
  fun getDownloadedModels(): Flow<List<DownloadedModelEntity>>

  @Query("SELECT * FROM downloaded_models WHERE id = :modelId LIMIT 1")
  suspend fun getModelById(modelId: String): DownloadedModelEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModel(model: DownloadedModelEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertInitialModels(models: List<DownloadedModelEntity>)

  @Update
  suspend fun updateModel(model: DownloadedModelEntity)

  @Query("UPDATE downloaded_models SET isDownloaded = :isDownloaded, downloadProgress = :progress, localFilePath = :filePath WHERE id = :modelId")
  suspend fun updateDownloadStatus(modelId: String, isDownloaded: Boolean, progress: Float, filePath: String?)

  @Query("UPDATE downloaded_models SET lastUsedTimestamp = :timestamp WHERE id = :modelId")
  suspend fun updateLastUsed(modelId: String, timestamp: Long)

  @Query("DELETE FROM downloaded_models WHERE id = :modelId")
  suspend fun deleteModel(modelId: String)
}
