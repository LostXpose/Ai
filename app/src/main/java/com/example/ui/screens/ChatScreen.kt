package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.ui.MainViewModel
import com.example.ui.components.LiveStatusDot
import com.example.ui.components.MetricPill
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  viewModel: MainViewModel,
  onNavigateToModels: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val metrics by viewModel.liveMetrics.collectAsState()
  val messages by viewModel.activeMessages.collectAsState()
  val context = LocalContext.current

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Scroll to bottom on new message or during generation
  LaunchedEffect(messages.size, uiState.streamingChunk) {
    if (messages.isNotEmpty() || uiState.streamingChunk.isNotEmpty()) {
      val targetIndex = (messages.size + if (uiState.streamingChunk.isNotEmpty()) 1 else 0) - 1
      if (targetIndex >= 0) {
        listState.animateScrollToItem(targetIndex)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Top HUD Bar - Real-time Performance Telemetry
    PerformanceTelemetryHeader(
      metrics = metrics,
      activeModelName = uiState.activeModelName,
      isCloudMode = settings.isCloudApiMode,
      onNewChat = { viewModel.createNewChat() },
      onClearChat = { viewModel.clearCurrentChat() },
      onSwitchModel = onNavigateToModels
    )

    // 2. Chat messages list or empty state
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      if (messages.isEmpty() && uiState.streamingChunk.isEmpty()) {
        ChatEmptyState(
          activeModel = uiState.activeModelName,
          isCloud = settings.isCloudApiMode,
          onSelectPrompt = { prompt ->
            inputText = prompt
            viewModel.sendMessage(prompt)
          }
        )
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
          contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(messages, key = { it.id }) { message ->
            ChatMessageBubble(
              message = message,
              onCopy = { text ->
                copyToClipboard(context, text)
                viewModel.showToast("Copied to clipboard")
              }
            )
          }

          if (uiState.streamingChunk.isNotEmpty()) {
            item(key = "streaming_live") {
              StreamingAssistantBubble(
                chunk = uiState.streamingChunk,
                modelName = uiState.activeModelName,
                metrics = metrics
              )
            }
          }
        }
      }
    }

    // 3. Bottom Input Bar
    ChatInputBar(
      inputText = inputText,
      onInputChanged = { inputText = it },
      isGenerating = uiState.isGenerating,
      onSend = {
        if (inputText.isNotBlank()) {
          val p = inputText
          inputText = ""
          viewModel.sendMessage(p)
        }
      },
      onStop = { viewModel.stopGeneration() }
    )
  }
}

@Composable
fun PerformanceTelemetryHeader(
  metrics: com.example.data.model.InferenceMetrics,
  activeModelName: String,
  isCloudMode: Boolean,
  onNewChat: () -> Unit,
  onClearChat: () -> Unit,
  onSwitchModel: () -> Unit
) {
  Surface(
    color = CyberDarkSurface,
    modifier = Modifier
      .fillMaxWidth()
      .border(BorderStroke(1.dp, CyberCardBorder.copy(alpha = 0.5f)))
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
      // Top row: Active model pill + action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF161B2E))
            .clickable { onSwitchModel() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          LiveStatusDot(isGenerating = metrics.isGenerating)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = activeModelName,
            color = if (isCloudMode) NeonAmber else NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isCloudMode) "☁️ CLOUD" else "⚡ LOCAL (0 TOK)",
            color = if (isCloudMode) NeonAmber else NeonGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNewChat,
            modifier = Modifier
              .size(36.dp)
              .testTag("new_chat_button")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "New Chat",
              tint = NeonCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onClearChat,
            modifier = Modifier
              .size(36.dp)
              .testTag("clear_chat_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Clear Chat",
              tint = TextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Second row: Live Hardware Telemetry Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          MetricPill(
            label = "INFERENCE",
            value = "${String.format("%.1f", metrics.tokensPerSecond)} tok/s",
            color = if (metrics.tokensPerSecond > 20) NeonGreen else NeonCyan,
            icon = Icons.Default.Speed
          )
        }
        item {
          MetricPill(
            label = "RAM USAGE",
            value = "${metrics.ramUsedMb}M / ${metrics.ramTotalMb}M",
            color = if (metrics.memoryPressure == "Critical") NeonAmber else NeonCyan,
            icon = Icons.Default.Memory
          )
        }
        item {
          MetricPill(
            label = "TEMP / BATTERY",
            value = "${metrics.batteryTempCelsius}°C (${metrics.batteryPercentage}%)",
            color = if (metrics.batteryTempCelsius > 38f) NeonAmber else TextCyan,
            icon = Icons.Default.Thermostat
          )
        }
        item {
          MetricPill(
            label = "TTFT",
            value = "${metrics.timeToFirstTokenMs} ms",
            color = NeonViolet,
            icon = Icons.Default.Bolt
          )
        }
      }
    }
  }
}

@Composable
fun ChatEmptyState(
  activeModel: String,
  isCloud: Boolean,
  onSelectPrompt: (String) -> Unit
) {
  val prompts = listOf(
    "পাইথনে একটি দ্রুত সার্চ অ্যালগরিদম লিখে দাও",
    "How does 4-bit GGUF quantization save phone RAM?",
    "Write a short cyberpunk AI poem in Bengali",
    "Explain Kotlin Coroutines StateFlow vs SharedFlow",
    "আমার ডিভাইসের লোকাল স্টোরেজে আর কোন কোন মডেল চলবে?"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(NeonCyan.copy(alpha = 0.12f))
        .border(1.5.dp, NeonCyan, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Psychology,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(36.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "NeonLLM Engine Ready",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = if (isCloud) "Cloud API Mode Active" else "100% Offline Local Neural Engine • No Tokens • Unlimited Free",
      fontSize = 12.sp,
      color = if (isCloud) NeonAmber else NeonGreen,
      fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(18.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(horizontal = 8.dp)
    ) {
      NeonBadge(text = "0 Tokens Billed", color = NeonGreen)
      NeonBadge(text = "Full Offline", color = NeonCyan)
      NeonBadge(text = "Privacy First", color = NeonViolet)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Tap a prompt to test inference speed:",
      fontSize = 12.sp,
      color = TextMuted,
      modifier = Modifier.align(Alignment.Start)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      for (prompt in prompts) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
            .clickable { onSelectPrompt(prompt) }
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "⚡", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = prompt,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 17.sp
          )
        }
      }
    }
  }
}

@Composable
fun ChatMessageBubble(
  message: ChatMessageEntity,
  onCopy: (String) -> Unit
) {
  val isUser = message.role == "user"

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    if (isUser) {
      // User bubble
      Box(
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
          .background(
            Brush.horizontalGradient(listOf(Color(0xFF2E1065), Color(0xFF1E1B4B)))
          )
          .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
          .padding(14.dp)
      ) {
        Text(
          text = message.content,
          color = TextPrimary,
          fontSize = 14.sp,
          lineHeight = 20.sp
        )
      }
    } else {
      // Assistant bubble
      NeonCard(
        modifier = Modifier.fillMaxWidth(0.95f),
        borderColor = NeonCyan.copy(alpha = 0.35f),
        backgroundColor = CyberCardBg
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Metadata row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              LiveStatusDot(isGenerating = false)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = message.modelName.ifBlank { "NeonLLM" },
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              if (message.tokPerSec > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "${String.format("%.1f", message.tokPerSec)} tok/s",
                  color = NeonGreen,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            IconButton(
              onClick = { onCopy(message.content) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy message",
                tint = TextMuted,
                modifier = Modifier.size(15.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Body text (handles <think> block if present)
          RenderAssistantContent(content = message.content)
        }
      }
    }
  }
}

@Composable
fun StreamingAssistantBubble(
  chunk: String,
  modelName: String,
  metrics: com.example.data.model.InferenceMetrics
) {
  NeonCard(
    modifier = Modifier.fillMaxWidth(0.95f),
    borderColor = NeonCyan,
    backgroundColor = CyberCardBg,
    glow = true
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            strokeWidth = 2.dp,
            color = NeonCyan
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generating ($modelName)",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Text(
          text = "${String.format("%.1f", metrics.tokensPerSecond)} tok/s",
          color = NeonGreen,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      RenderAssistantContent(content = chunk)

      Spacer(modifier = Modifier.height(6.dp))

      // Pulsing cursor
      Text(
        text = "▮",
        color = NeonCyan,
        fontSize = 14.sp
      )
    }
  }
}

@Composable
fun RenderAssistantContent(content: String) {
  var showThinking by remember { mutableStateOf(false) }

  if (content.contains("<think>") && content.contains("</think>")) {
    val thinkPart = content.substringAfter("<think>").substringBefore("</think>").trim()
    val answerPart = content.substringAfter("</think>").trim()

    Column {
      // Expandable Thinking block
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF0F1322))
          .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .clickable { showThinking = !showThinking }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🧠", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DeepSeek Reasoning Process",
            color = NeonViolet,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
        Icon(
          imageVector = if (showThinking) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = NeonViolet,
          modifier = Modifier.size(16.dp)
        )
      }

      AnimatedVisibility(visible = showThinking) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0B0D18))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Text(
            text = thinkPart,
            color = TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = answerPart,
        color = TextPrimary,
        fontSize = 14.sp,
        lineHeight = 21.sp
      )
    }
  } else {
    Text(
      text = content,
      color = TextPrimary,
      fontSize = 14.sp,
      lineHeight = 21.sp
    )
  }
}

@Composable
fun ChatInputBar(
  inputText: String,
  onInputChanged: (String) -> Unit,
  isGenerating: Boolean,
  onSend: () -> Unit,
  onStop: () -> Unit
) {
  Surface(
    color = CyberDarkSurface,
    modifier = Modifier
      .fillMaxWidth()
      .border(BorderStroke(1.dp, CyberCardBorder))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = onInputChanged,
        placeholder = {
          Text(
            text = "Ask anything in Bengali or English...",
            color = TextMuted,
            fontSize = 13.sp
          )
        },
        modifier = Modifier
          .weight(1f)
          .testTag("chat_input_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = CyberCardBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          cursorColor = NeonCyan,
          focusedContainerColor = CyberCardBg,
          unfocusedContainerColor = CyberCardBg
        ),
        shape = RoundedCornerShape(24.dp),
        maxLines = 4
      )

      Spacer(modifier = Modifier.width(8.dp))

      if (isGenerating) {
        IconButton(
          onClick = onStop,
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(NeonMagenta.copy(alpha = 0.2f))
            .border(1.5.dp, NeonMagenta, CircleShape)
            .testTag("stop_generation_button")
        ) {
          Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop generation",
            tint = NeonMagenta,
            modifier = Modifier.size(24.dp)
          )
        }
      } else {
        IconButton(
          onClick = onSend,
          enabled = inputText.isNotBlank(),
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank()) NeonCyan else CyberCardBg)
            .border(1.dp, if (inputText.isNotBlank()) NeonCyan else CyberCardBorder, CircleShape)
            .testTag("send_prompt_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send prompt",
            tint = if (inputText.isNotBlank()) Color.Black else TextMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

private fun copyToClipboard(context: Context, text: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText("NeonLLM Response", text)
  clipboard.setPrimaryClip(clip)
}
