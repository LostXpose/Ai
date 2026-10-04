package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AiAgent
import com.example.ui.MainViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentsScreen(
  viewModel: MainViewModel,
  onNavigateToChat: (starterPrompt: String?) -> Unit
) {
  val agents by viewModel.allAgents.collectAsState()
  val activeAgent by viewModel.activeAgent.collectAsState()
  val uiState by viewModel.uiState.collectAsState()

  var showCreateDialog by remember { mutableStateOf(false) }

  // Storage permission launcher
  val storagePermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val granted = permissions.values.any { it }
    viewModel.updateStoragePermission(granted)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberBlack)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberDarkSurface)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.SmartToy,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "AI Agents Fleet",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Specialized on-device & cloud autonomous agents",
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }

      Button(
        onClick = { showCreateDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("create_agent_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(4.dp))
        Text("+ Create", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item { Spacer(modifier = Modifier.height(6.dp)) }

      // 1. Hero Card featuring "Robot Drinking Water"
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
          colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_robot_water),
                contentDescription = "Cyberpunk Robot drinking water",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )

              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, CyberDarkSurface.copy(alpha = 0.95f)),
                      startY = 80f
                    )
                  )
              )

              Row(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                NeonBadge(
                  text = "⚡ HYDRATED EDGE AI",
                  color = NeonCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                NeonBadge(
                  text = "0% THERMAL THROTTLE",
                  color = NeonGreen
                )
              }
            }

            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Meet AquaBot Zero & The Agent Collective",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Autonomous agents run directly on your hardware or through cloud streaming. Each agent carries distinct system architectures, prompts, and reasoning skills.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )
            }
          }
        }
      }

      // 2. Storage Permission Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.dp,
              if (uiState.hasStoragePermission) NeonGreen.copy(alpha = 0.4f) else NeonAmber.copy(alpha = 0.4f),
              RoundedCornerShape(12.dp)
            ),
          colors = CardDefaults.cardColors(containerColor = CyberCardBg),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (uiState.hasStoragePermission) Icons.Default.CheckCircle else Icons.Default.FolderSpecial,
                contentDescription = null,
                tint = if (uiState.hasStoragePermission) NeonGreen else NeonAmber,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Storage & Local Model Access",
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = if (uiState.hasStoragePermission)
                    "Permission active. GGUF weights read/write allowed."
                  else
                    "Grant permission to load GGUF weights & save exports.",
                  color = TextMuted,
                  fontSize = 12.sp
                )
              }
            }

            if (!uiState.hasStoragePermission) {
              Button(
                onClick = {
                  val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                  } else {
                    arrayOf(
                      Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                  }
                  storagePermissionLauncher.launch(perms)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("grant_storage_perm_button")
              ) {
                Text("Allow", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            } else {
              NeonBadge(text = "GRANTED", color = NeonGreen)
            }
          }
        }
      }

      // 3. Section Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Select Active Agent (${agents.size})",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Active: ${activeAgent.name}",
            color = Color(activeAgent.glowColorHex),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // 4. Agent Cards List
      items(agents, key = { it.id }) { agent ->
        val isSelected = agent.id == activeAgent.id
        val agentColor = Color(agent.glowColorHex)

        NeonCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("agent_card_${agent.id}"),
          borderColor = if (isSelected) agentColor else CyberCardBorder,
          backgroundColor = if (isSelected) agentColor.copy(alpha = 0.08f) else CyberCardBg
        ) {
          Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Avatar or image
                if (agent.imageResId != null) {
                  Image(
                    painter = painterResource(id = agent.imageResId),
                    contentDescription = agent.name,
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .border(1.5.dp, agentColor, CircleShape),
                    contentScale = ContentScale.Crop
                  )
                } else {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(agentColor.copy(alpha = 0.15f))
                      .border(1.5.dp, agentColor, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = getAgentIcon(agent.iconName),
                      contentDescription = null,
                      tint = agentColor,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = agent.name,
                      color = TextPrimary,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Bold
                    )
                    if (isSelected) {
                      Spacer(modifier = Modifier.width(6.dp))
                      NeonBadge(text = "ACTIVE", color = agentColor)
                    }
                  }
                  Text(
                    text = agent.role,
                    color = agentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              if (agent.isCustom) {
                IconButton(onClick = { viewModel.deleteAgent(agent.id) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete agent", tint = TextMuted, modifier = Modifier.size(18.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = agent.description,
              color = TextSecondary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              agent.tags.forEach { tag ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberDarkSurface)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(text = "#$tag", color = TextMuted, fontSize = 11.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Starter prompts
            Text(
              text = "Quick Tasks:",
              color = TextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              agent.starterPrompts.take(2).forEach { starter ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberDarkSurface)
                    .border(0.5.dp, agentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                    .clickable {
                      viewModel.selectAgent(agent.id)
                      onNavigateToChat(starter)
                    }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = agentColor,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = starter,
                      color = TextPrimary,
                      fontSize = 12.sp,
                      maxLines = 1
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              if (!isSelected) {
                OutlinedButton(
                  onClick = { viewModel.selectAgent(agent.id) },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = agentColor),
                  border = androidx.compose.foundation.BorderStroke(1.dp, agentColor)
                ) {
                  Text("Activate Agent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }

              Button(
                onClick = {
                  viewModel.selectAgent(agent.id)
                  onNavigateToChat(null)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) agentColor else CyberDarkSurface)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Chat,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isSelected) Color.Black else TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Chat Now",
                  color = if (isSelected) Color.Black else TextPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(24.dp)) }
    }
  }

  // Create Custom Agent Dialog
  if (showCreateDialog) {
    CreateAgentDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { newAgent ->
        viewModel.createCustomAgent(newAgent)
        showCreateDialog = false
      }
    )
  }
}

@Composable
private fun CreateAgentDialog(
  onDismiss: () -> Unit,
  onCreate: (AiAgent) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var systemPrompt by remember { mutableStateOf("") }
  var temperature by remember { mutableFloatStateOf(0.7f) }
  var starter1 by remember { mutableStateOf("") }
  var selectedColorHex by remember { mutableStateOf(0xFF00F5FF) }

  val colorOptions = listOf(
    0xFF00F5FF to "Cyan",
    0xFF00FF9D to "Green",
    0xFFFF007F to "Magenta",
    0xFFBF00FF to "Violet",
    0xFFFFB703 to "Amber"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "⚡ Create Custom AI Agent",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Agent Name (e.g. RoboMentor)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = role,
          onValueChange = { role = it },
          label = { Text("Specialty Role") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = systemPrompt,
          onValueChange = { systemPrompt = it },
          label = { Text("System Instructions") },
          placeholder = { Text("Define personality, expertise & instructions") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder
          ),
          modifier = Modifier.fillMaxWidth(),
          maxLines = 4
        )

        OutlinedTextField(
          value = starter1,
          onValueChange = { starter1 = it },
          label = { Text("Sample Prompt") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Text(
          text = "Creativity (Temperature: ${"%.2f".format(temperature)})",
          color = TextSecondary,
          fontSize = 12.sp
        )
        Slider(
          value = temperature,
          onValueChange = { temperature = it },
          valueRange = 0.1f..1.2f,
          colors = SliderDefaults.colors(
            thumbColor = NeonCyan,
            activeTrackColor = NeonCyan
          )
        )

        // Color chips
        Text(text = "Theme Accent:", color = TextSecondary, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          colorOptions.forEach { (colorHex, _) ->
            val color = Color(colorHex)
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                  2.dp,
                  if (selectedColorHex == colorHex) Color.White else Color.Transparent,
                  CircleShape
                )
                .clickable { selectedColorHex = colorHex }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val agent = AiAgent(
              id = "custom_" + UUID.randomUUID().toString().take(8),
              name = name.trim(),
              role = if (role.isNotBlank()) role.trim() else "Custom Autonomous Assistant",
              tagline = "User-created custom agent",
              description = if (description.isNotBlank()) description.trim() else "Custom AI agent created with tailored system rules.",
              systemPrompt = if (systemPrompt.isNotBlank()) systemPrompt.trim() else "You are $name, a helpful and capable AI assistant.",
              glowColorHex = selectedColorHex,
              starterPrompts = if (starter1.isNotBlank()) listOf(starter1.trim()) else listOf("Hello! How can you help me today?"),
              tags = listOf("Custom", "Autonomous"),
              temperature = temperature,
              isCustom = true
            )
            onCreate(agent)
          }
        },
        enabled = name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
      ) {
        Text("Save & Activate", color = Color.Black, fontWeight = FontWeight.Bold)
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

private fun getAgentIcon(name: String): ImageVector {
  return when (name) {
    "water_drop" -> Icons.Default.WaterDrop
    "terminal" -> Icons.Default.Terminal
    "psychology" -> Icons.Default.Psychology
    "auto_stories" -> Icons.Default.AutoStories
    "security" -> Icons.Default.Security
    else -> Icons.Default.SmartToy
  }
}
