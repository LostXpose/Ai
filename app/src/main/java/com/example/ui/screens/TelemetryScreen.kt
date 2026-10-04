package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TelemetryScreen(viewModel: MainViewModel) {
  val metrics by viewModel.liveMetrics.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val uiState by viewModel.uiState.collectAsState()

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
        imageVector = Icons.Default.AutoGraph,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Real-Time Telemetry & Hardware HUD",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Zero Cloud Leakage • Physical On-Device Profiling",
          color = NeonGreen,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Live Speed & Latency Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonCyan,
          backgroundColor = CyberCardBg,
          glow = true
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "INFERENCE VELOCITY",
                  color = TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              }
              NeonBadge(
                text = if (metrics.isGenerating) "RUNNING" else "STANDBY",
                color = if (metrics.isGenerating) NeonCyan else NeonGreen
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = String.format("%.1f", metrics.tokensPerSecond),
                  color = NeonCyan,
                  fontSize = 32.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "TOKENS / SEC",
                  color = TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Box(
                modifier = Modifier
                  .width(1.dp)
                  .height(44.dp)
                  .background(CyberCardBorder)
              )

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${metrics.timeToFirstTokenMs}",
                  color = NeonViolet,
                  fontSize = 32.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "TTFT (LATENCY MS)",
                  color = TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Engine State: ${metrics.engineStatus}",
              color = TextMuted,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // 2. RAM & Hardware Pressure Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberCardBorder,
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
                  imageVector = Icons.Default.Memory,
                  contentDescription = null,
                  tint = NeonViolet,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "DEVICE RAM MATRIX",
                  color = TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              }
              NeonBadge(
                text = "Pressure: ${metrics.memoryPressure}",
                color = when (metrics.memoryPressure) {
                  "Critical" -> NeonAmber
                  "Moderate" -> NeonViolet
                  else -> NeonGreen
                }
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val ramRatio = if (metrics.ramTotalMb > 0) {
              metrics.ramUsedMb.toFloat() / metrics.ramTotalMb.toFloat()
            } else 0.4f

            LinearProgressIndicator(
              progress = { ramRatio },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (ramRatio > 0.85f) NeonAmber else NeonViolet,
              trackColor = Color(0xFF141828)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Used: ${metrics.ramUsedMb} MB",
                color = TextPrimary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "Free: ${metrics.ramFreeMb} MB",
                color = NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "Total: ${metrics.ramTotalMb} MB",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // 3. Thermal & Battery Card
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberCardBorder,
          backgroundColor = CyberCardBg
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Thermostat,
                contentDescription = null,
                tint = NeonAmber,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Battery Temperature",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Level: ${metrics.batteryPercentage}% • Healthy",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
              }
            }

            Text(
              text = "${metrics.batteryTempCelsius}°C",
              color = if (metrics.batteryTempCelsius > 38f) NeonAmber else NeonCyan,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // 4. Multi-thread CPU Core Allocation
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberCardBorder,
          backgroundColor = CyberCardBg
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ARM CPU Inference Threads",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Tuned for big.LITTLE performance cores",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }

              NeonBadge(
                text = "${settings.cpuThreads} Threads",
                color = NeonCyan
              )
            }

            Slider(
              value = settings.cpuThreads.toFloat(),
              onValueChange = { newVal ->
                viewModel.updateSettings { it.copy(cpuThreads = newVal.toInt()) }
              },
              valueRange = 2f..8f,
              steps = 5,
              colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = Color(0xFF1B2238)
              ),
              modifier = Modifier.testTag("cpu_threads_slider")
            )
          }
        }
      }

      // 5. Raw Hardware Benchmark Tool
      item {
        NeonCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonMagenta.copy(alpha = 0.5f),
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
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = NeonMagenta,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ON-DEVICE NEURAL BENCHMARK",
                  color = NeonMagenta,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Runs a dry 120-token local forward pass to measure your phone's peak ARM NEON computation speed.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            uiState.benchmarkScoreTokSec?.let { score ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF1E112A))
                  .border(1.dp, NeonMagenta, RoundedCornerShape(8.dp))
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Peak Throughput Result",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Phone Tier: Ultra Fast Edge AI",
                    color = NeonGreen,
                    fontSize = 11.sp
                  )
                }

                Text(
                  text = "${String.format("%.1f", score)} tok/s",
                  color = NeonMagenta,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
              onClick = { viewModel.runHardwareBenchmark() },
              enabled = !uiState.benchmarkRunning,
              colors = ButtonDefaults.buttonColors(
                containerColor = NeonMagenta,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("run_benchmark_button")
            ) {
              if (uiState.benchmarkRunning) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stressing Local Cores...", fontSize = 13.sp)
              } else {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Start Neural Benchmark",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
