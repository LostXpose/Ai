package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DocsScreen
import com.example.ui.screens.ModelsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TelemetryScreen
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun NeonLLMApp(viewModel: MainViewModel) {
  var currentScreen by rememberSaveable { mutableStateOf(Screen.CHAT) }
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .border(1.dp, CyberCardBorder.copy(alpha = 0.5f)),
        containerColor = CyberDarkSurface,
        tonalElevation = 8.dp
      ) {
        Screen.values().forEach { screen ->
          val isSelected = currentScreen == screen
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentScreen = screen },
            icon = {
              Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                tint = if (isSelected) NeonCyan else TextMuted
              )
            },
            label = {
              Text(
                text = screen.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NeonCyan else TextMuted
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = Color(0xFF142436)
            ),
            modifier = Modifier.testTag(screen.testTag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        Screen.CHAT -> ChatScreen(
          viewModel = viewModel,
          onNavigateToModels = { currentScreen = Screen.MODELS }
        )
        Screen.MODELS -> ModelsScreen(
          viewModel = viewModel,
          onNavigateToChat = { currentScreen = Screen.CHAT }
        )
        Screen.TELEMETRY -> TelemetryScreen(viewModel = viewModel)
        Screen.DOCS -> DocsScreen()
        Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
      }

      // Toast / Info Banner Overlay
      AnimatedVisibility(
        visible = uiState.infoMessage != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        uiState.infoMessage?.let { msg ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF10192A))
              .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Text(
              text = msg,
              color = NeonCyan,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}
