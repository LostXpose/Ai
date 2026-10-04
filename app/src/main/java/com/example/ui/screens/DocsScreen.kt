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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.DefaultModelsRegistry
import com.example.data.model.LLMModelInfo
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DocsScreen() {
  var searchQuery by remember { mutableStateOf("") }
  var expandedModelId by remember { mutableStateOf<String?>(null) }

  val filteredModels = DefaultModelsRegistry.models.filter {
    it.name.contains(searchQuery, ignoreCase = true) ||
      it.family.contains(searchQuery, ignoreCase = true) ||
      it.description.contains(searchQuery, ignoreCase = true) ||
      it.author.contains(searchQuery, ignoreCase = true)
  }

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
        imageVector = Icons.AutoMirrored.Filled.MenuBook,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Offline Model Docs & GGUF Guide",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "100% Offline Reference • Quantization & Specs",
          color = NeonGreen,
          fontSize = 11.sp
        )
      }
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search models, specs, benchmarks, quantization...", fontSize = 12.sp, color = TextMuted) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
      },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan,
        unfocusedBorderColor = CyberCardBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = CyberCardBg,
        unfocusedContainerColor = CyberCardBg
      ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp)
        .testTag("docs_search_field")
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Architecture Overview Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonCyan.copy(alpha = 0.4f),
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Why Offline Local LLMs Have 0 Token Cost",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Cloud APIs (OpenAI, Gemini) charge per token because computations happen on remote data center servers. With NeonLLM, model neural weights run natively on your phone's processor using 4-bit integer matrix math (ARM NEON instructions). As a result: completely free, unlimited usage, and 100% private.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
          }
        }
      }

      // 2. Quantization Bible
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonViolet.copy(alpha = 0.4f),
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "What is Q4_K_M Quantization?",
              color = NeonViolet,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Standard models use 16-bit floating point numbers (FP16). Q4_K_M compresses weights to 4-bits with medium k-quant block distribution. It shrinks memory footprint by ~75% (e.g. 7GB down to 1.5GB) with negligible loss in reasoning capability.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
          }
        }
      }

      // 3. Models Encyclopedia Header
      item {
        Text(
          text = "ALL MODELS ENCYCLOPEDIA & BENCHMARKS",
          color = TextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          modifier = Modifier.padding(top = 6.dp)
        )
      }

      // 4. Model Spec Cards
      items(filteredModels, key = { it.id }) { model ->
        val isExpanded = expandedModelId == model.id

        ModelDocsCard(
          model = model,
          isExpanded = isExpanded,
          onToggle = {
            expandedModelId = if (isExpanded) null else model.id
          }
        )
      }
    }
  }
}

@Composable
fun ModelDocsCard(
  model: LLMModelInfo,
  isExpanded: Boolean,
  onToggle: () -> Unit
) {
  NeonCard(
    modifier = Modifier.fillMaxWidth(),
    borderColor = if (isExpanded) NeonCyan else CyberCardBorder,
    backgroundColor = CyberCardBg
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onToggle() }
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = model.name,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${model.author} • ${model.parameterCount} • ${model.quantization}",
            color = TextMuted,
            fontSize = 11.sp
          )
        }

        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = if (isExpanded) NeonCyan else TextMuted,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        NeonBadge(text = "RAM: ${model.minRamMb}MB+", color = NeonCyan)
        NeonBadge(text = "Ctx: ${model.contextLength}", color = NeonAmber)
        NeonBadge(text = "MMLU: ${model.benchmarkMmlu}%", color = NeonGreen)
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          Text(
            text = "Architecture & Deep-Dive:",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = model.docsOverview,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Bengali Fluency Rating: ${model.bengaliSupportRating}",
            color = NeonViolet,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Key Architectural Strengths:",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          for (s in model.strengths) {
            Text(
              text = "• $s",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Recommended System Prompt:",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF0F121E))
              .border(1.dp, CyberCardBorder, RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Text(
              text = model.recommendedSystemPrompt,
              color = NeonCyan,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}
