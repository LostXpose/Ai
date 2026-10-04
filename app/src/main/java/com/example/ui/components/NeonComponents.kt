package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NeonCard(
  modifier: Modifier = Modifier,
  borderColor: Color = NeonCyan.copy(alpha = 0.4f),
  backgroundColor: Color = CyberCardBg,
  glow: Boolean = false,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val shape = RoundedCornerShape(16.dp)
  val border = BorderStroke(
    width = 1.dp,
    brush = if (glow) {
      Brush.horizontalGradient(
        listOf(NeonCyan, NeonViolet, NeonMagenta)
      )
    } else {
      Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.15f)))
    }
  )

  Card(
    modifier = modifier
      .clip(shape)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    shape = shape,
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = border
  ) {
    content()
  }
}

@Composable
fun NeonBadge(
  text: String,
  color: Color = NeonCyan,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(color.copy(alpha = 0.12f))
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(
      text = text,
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun MetricPill(
  label: String,
  value: String,
  color: Color = NeonCyan,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF0F121C))
      .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
    }
    Column {
      Text(
        text = label,
        color = TextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 10.sp
      )
      Text(
        text = value,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        lineHeight = 14.sp
      )
    }
  }
}

@Composable
fun LiveStatusDot(
  isGenerating: Boolean,
  modifier: Modifier = Modifier
) {
  val targetColor = if (isGenerating) NeonCyan else NeonGreen
  val color by animateColorAsState(targetColor, label = "dotColor")

  Box(
    modifier = modifier
      .size(10.dp)
      .clip(CircleShape)
      .background(color)
      .border(1.5.dp, color.copy(alpha = 0.4f), CircleShape)
  )
}
