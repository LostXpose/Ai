package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DownloadedModelEntity
import com.example.ui.MainViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ModelsScreen(
  viewModel: MainViewModel,
  onNavigateToChat: () -> Unit
) {
  val allModels by viewModel.allModels.collectAsState()
  val uiState by viewModel.uiState.collectAsState()
  val settings by viewModel.settings.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  var showImportDialog by remember { mutableStateOf(false) }

  val filteredModels = when (selectedTab) {
    0 -> allModels
    1 -> allModels.filter { it.isDownloaded }
    else -> allModels.filter { it.id == settings.activeModelId }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Header with title & Add Custom model button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberDarkSurface)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = "Local Model Hub",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Unlimited • 0 Tokens • Local Storage",
          color = NeonGreen,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Button(
        onClick = { showImportDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("import_custom_model_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = Color.Black,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Import GGUF",
          color = Color.Black,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // 2. Storage Inspector Card
    uiState.storageBreakdown?.let { storage ->
      StorageInspectorCard(
        breakdown = storage,
        downloadedCount = allModels.count { it.isDownloaded }
      )
    }

    // 3. Filter Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = CyberDarkSurface,
      contentColor = NeonCyan,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = NeonCyan,
          height = 2.5.dp
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("All Hub (${allModels.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            "Local (${allModels.count { it.isDownloaded }})",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Active", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
      )
    }

    // 4. Models List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredModels, key = { it.id }) { model ->
        val isActive = model.id == settings.activeModelId
        val downloadSpeed = uiState.activeDownloadSpeed[model.id]

        ModelItemCard(
          model = model,
          isActive = isActive,
          downloadSpeed = downloadSpeed,
          onSetActive = {
            viewModel.setActiveModel(model)
            onNavigateToChat()
          },
          onDownload = { viewModel.startModelDownload(model.id) },
          onCancel = { viewModel.cancelModelDownload(model.id) },
          onDelete = { viewModel.deleteModelFromStorage(model.id) }
        )
      }
    }
  }

  // Custom model import dialog
  if (showImportDialog) {
    ImportCustomModelDialog(
      onDismiss = { showImportDialog = false },
      onConfirm = { name, author, urlOrPath, sizeMb ->
        viewModel.importCustomModel(name, author, urlOrPath, sizeMb)
        showImportDialog = false
      }
    )
  }
}

@Composable
fun StorageInspectorCard(
  breakdown: com.example.data.repository.StorageBreakdown,
  downloadedCount: Int
) {
  NeonCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp),
    borderColor = NeonCyan.copy(alpha = 0.3f),
    backgroundColor = CyberCardBg
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Local On-Device Storage",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        NeonBadge(
          text = "$downloadedCount Ready Offline",
          color = NeonGreen
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress bar
      LinearProgressIndicator(
        progress = { breakdown.usedPercent.coerceIn(0.05f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = NeonCyan,
        trackColor = Color(0xFF161B2E)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Models Stored: ${breakdown.formattedNeonModels}",
          color = NeonCyan,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Free Device: ${breakdown.formattedFree}",
          color = NeonGreen,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
fun ModelItemCard(
  model: DownloadedModelEntity,
  isActive: Boolean,
  downloadSpeed: String?,
  onSetActive: () -> Unit,
  onDownload: () -> Unit,
  onCancel: () -> Unit,
  onDelete: () -> Unit
) {
  val isDownloading = model.downloadProgress > 0f && model.downloadProgress < 1.0f

  NeonCard(
    modifier = Modifier.fillMaxWidth(),
    borderColor = when {
      isActive -> NeonCyan
      model.isDownloaded -> NeonViolet.copy(alpha = 0.5f)
      else -> CyberCardBorder
    },
    backgroundColor = CyberCardBg,
    glow = isActive
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = model.name,
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            if (isActive) {
              Spacer(modifier = Modifier.width(6.dp))
              NeonBadge(text = "ACTIVE", color = NeonCyan)
            }
          }
          Text(
            text = "by ${model.author} • ${model.parameterCount}",
            color = TextMuted,
            fontSize = 11.sp
          )
        }

        NeonBadge(
          text = model.formattedSize,
          color = if (model.isDownloaded) NeonGreen else NeonCyan
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = model.description,
        color = TextSecondary,
        fontSize = 12.sp,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        NeonBadge(text = model.quantization, color = NeonViolet)
        NeonBadge(text = "RAM: ~${model.ramRequiredMb} MB", color = TextCyan)
        NeonBadge(text = "Ctx: ${model.contextLength}", color = NeonAmber)
      }

      // Download Progress bar
      if (isDownloading) {
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
          progress = { model.downloadProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = NeonCyan,
          trackColor = Color(0xFF1A2035)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Downloading: ${(model.downloadProgress * 100).toInt()}%",
            color = NeonCyan,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = downloadSpeed ?: "12.4 MB/s",
            color = NeonGreen,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (model.isDownloaded) {
          if (!model.isPreloaded) {
            OutlinedButton(
              onClick = onDelete,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Delete", fontSize = 11.sp)
            }
          }

          Button(
            onClick = onSetActive,
            enabled = !isActive,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isActive) Color(0xFF1C2237) else NeonCyan,
              contentColor = if (isActive) NeonCyan else Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isActive) "Active Engine" else "Select Model",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else if (isDownloading) {
          Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(alpha = 0.2f), contentColor = NeonRed),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Cancel Download", fontSize = 12.sp)
          }
        } else {
          Button(
            onClick = onDownload,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Download (${model.formattedSize})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun ImportCustomModelDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, Long) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var author by remember { mutableStateOf("") }
  var urlOrPath by remember { mutableStateOf("") }
  var sizeMbText by remember { mutableStateOf("1200") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Register Custom GGUF Model",
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Enter a direct HuggingFace GGUF link or device file path to manage in your offline storage.",
          color = TextSecondary,
          fontSize = 12.sp
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Model Name (e.g. My-Llama-Custom)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = author,
          onValueChange = { author = it },
          label = { Text("Author / Organization") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = urlOrPath,
          onValueChange = { urlOrPath = it },
          label = { Text("HuggingFace URL or /sdcard/model.gguf") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = sizeMbText,
          onValueChange = { sizeMbText = it },
          label = { Text("Estimated Size in MB") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val sizeMb = sizeMbText.toLongOrNull() ?: 1000L
            onConfirm(name, author, urlOrPath, sizeMb)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
      ) {
        Text("Save Model", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    },
    containerColor = CyberDarkSurface
  )
}
