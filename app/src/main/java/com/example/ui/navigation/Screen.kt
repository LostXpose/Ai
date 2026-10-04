package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
  val route: String,
  val title: String,
  val icon: ImageVector,
  val testTag: String
) {
  CHAT("chat", "Chat", Icons.AutoMirrored.Filled.Chat, "nav_chat"),
  AGENTS("agents", "AI Agents", Icons.Default.SmartToy, "nav_agents"),
  MODELS("models", "Models & Storage", Icons.Default.Download, "nav_models"),
  TELEMETRY("telemetry", "Hardware HUD", Icons.Default.AutoGraph, "nav_telemetry"),
  DOCS("docs", "Docs", Icons.AutoMirrored.Filled.MenuBook, "nav_docs"),
  SETTINGS("settings", "Settings", Icons.Default.Settings, "nav_settings")
}
