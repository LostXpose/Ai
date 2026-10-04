package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.ThemeMode
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
  val settings by viewModel.settings.collectAsState()
  var showClearConfirm by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberDarkSurface)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Preferences & Engine Config",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Neon Dark Themes • API Key • Model Hyperparameters",
          color = TextMuted,
          fontSize = 11.sp
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Dark Theme Selector
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonCyan.copy(alpha = 0.5f),
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Brightness4,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "NEON DARK THEME MODE",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ThemeMode.values().forEach { mode ->
              val isSelected = settings.themeMode == mode

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) Color(0xFF131A2D) else Color.Transparent)
                  .border(
                    1.dp,
                    if (isSelected) NeonCyan else CyberCardBorder,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { viewModel.setThemeMode(mode) }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = mode.title,
                    color = if (isSelected) NeonCyan else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = mode.subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                  )
                }

                Box(
                  modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NeonCyan else Color.Transparent)
                    .border(2.dp, if (isSelected) NeonCyan else TextMuted, CircleShape)
                )
              }
            }
          }
        }
      }

      // 2. Mode Toggle: Offline Local vs Cloud API Mode
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = if (settings.isCloudApiMode) NeonAmber else NeonGreen,
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (settings.isCloudApiMode) Icons.Default.Cloud else Icons.Default.Lock,
                  contentDescription = null,
                  tint = if (settings.isCloudApiMode) NeonAmber else NeonGreen,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = if (settings.isCloudApiMode) "Cloud API Mode" else "Local Offline Engine",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = if (settings.isCloudApiMode) "Uses Google Gemini API" else "0 Tokens • 100% Free & Unlimited",
                    color = if (settings.isCloudApiMode) NeonAmber else NeonGreen,
                    fontSize = 11.sp
                  )
                }
              }

              Switch(
                checked = settings.isCloudApiMode,
                onCheckedChange = { viewModel.setCloudApiMode(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = NeonAmber,
                  checkedTrackColor = Color(0xFF3B2D12),
                  uncheckedThumbColor = NeonGreen,
                  uncheckedTrackColor = Color(0xFF10281F)
                ),
                modifier = Modifier.testTag("cloud_mode_switch")
              )
            }

            if (settings.isCloudApiMode) {
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "Custom Gemini API Key:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = settings.geminiApiKey,
                onValueChange = { newVal ->
                  viewModel.updateSettings { it.copy(geminiApiKey = newVal) }
                },
                placeholder = { Text("Leave blank to use pre-configured BuildConfig key", fontSize = 11.sp, color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.Key, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = NeonAmber,
                  unfocusedBorderColor = CyberCardBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      }

      // 3. Generation Hyperparameters
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberCardBorder,
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = NeonViolet,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "NEURAL SAMPLING PARAMETERS",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Temperature
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Temperature (Creativity):", color = TextSecondary, fontSize = 12.sp)
              Text(
                text = String.format("%.2f", settings.temperature),
                color = NeonCyan,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            }
            Slider(
              value = settings.temperature,
              onValueChange = { newVal ->
                viewModel.updateSettings { it.copy(temperature = newVal) }
              },
              valueRange = 0.1f..1.5f,
              colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Max Tokens
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Max Output Tokens:", color = TextSecondary, fontSize = 12.sp)
              Text(
                text = "${settings.maxTokens}",
                color = NeonViolet,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            }
            Slider(
              value = settings.maxTokens.toFloat(),
              onValueChange = { newVal ->
                viewModel.updateSettings { it.copy(maxTokens = newVal.toInt()) }
              },
              valueRange = 256f..4096f,
              steps = 7,
              colors = SliderDefaults.colors(
                thumbColor = NeonViolet,
                activeTrackColor = NeonViolet
              )
            )
          }
        }
      }

      // 4. System Prompt Config
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberCardBorder,
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "System Instruction (Personality):",
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = settings.systemPrompt,
              onValueChange = { newVal ->
                viewModel.updateSettings { it.copy(systemPrompt = newVal) }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth(),
              maxLines = 4
            )
          }
        }
      }

      // 5. Danger Zone: Clear History
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonRed.copy(alpha = 0.3f),
          backgroundColor = CyberCardBg
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Local Chat History",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Permanently wipes all Room chat records",
                color = TextMuted,
                fontSize = 11.sp
              )
            }

            Button(
              onClick = { showClearConfirm = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = NeonRed.copy(alpha = 0.2f),
                contentColor = NeonRed
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("clear_history_button")
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Wipe All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Confirm Clear All History", color = TextPrimary) },
      text = { Text("Are you sure you want to delete all local chat messages and sessions? This cannot be undone.", color = TextSecondary) },
      confirmButton = {
        Button(
          onClick = {
            viewModel.chatRepo.let {
              viewModel.clearCurrentChat()
              viewModel.createNewChat()
            }
            showClearConfirm = false
            viewModel.showToast("All chat history wiped.")
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
        ) {
          Text("Delete Everything", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirm = false }) {
          Text("Cancel", color = TextMuted)
        }
      },
      containerColor = CyberDarkSurface
    )
  }
}
